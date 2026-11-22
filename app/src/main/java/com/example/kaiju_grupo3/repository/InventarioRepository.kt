package com.example.kaiju_grupo3.repository

import com.example.kaiju_grupo3.data.KaijuDatabase
import com.example.kaiju_grupo3.data.MovimientoDao
import com.example.kaiju_grupo3.data.ProductoDao
import com.example.kaiju_grupo3.model.Movimiento
import com.example.kaiju_grupo3.model.Producto
import com.example.kaiju_grupo3.model.TipoMovimiento
import com.example.kaiju_grupo3.remote.KaijuApi
import com.example.kaiju_grupo3.remote.KaijuClient
import com.example.kaiju_grupo3.remote.dto.ErrorDto
import com.example.kaiju_grupo3.remote.dto.MovimientoRequestDto
import com.example.kaiju_grupo3.remote.dto.aMovimiento
import com.example.kaiju_grupo3.remote.dto.aProducto
import com.example.kaiju_grupo3.remote.dto.aRequest
import com.google.gson.Gson
import com.google.gson.JsonParseException
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException

// Ya existe otro producto con ese código (el servidor respondió 409).
class CodigoDuplicadoException(codigo: String) :
    Exception("Ya existe un producto con el código $codigo")

// No se pudo hablar con el servidor (sin internet, servidor apagado, tiempo agotado).
class SinConexionException : Exception("No se pudo conectar con el servidor")

// El servidor respondió con un error. "mensaje" es el texto que envía el backend.
class ErrorServidorException(
    val codigo: Int,
    mensaje: String,
    val campos: Map<String, String>? = null
) : Exception(mensaje)

// Repositorio: único punto de acceso a los datos del inventario.
//  - El backend (Spring Boot, por Retrofit) es la fuente de verdad.
//  - Room es una copia local: las pantallas siempre leen de Room, así la app sigue mostrando
//    datos si el servidor no responde, y se actualiza sola cuando Room cambia (Flow).
class InventarioRepository(
    private val productoDao: ProductoDao,
    private val movimientoDao: MovimientoDao,
    private val api: KaijuApi
) {

    constructor(db: KaijuDatabase, api: KaijuApi = KaijuClient.api) :
        this(db.productoDao(), db.movimientoDao(), api)

    val productos: Flow<List<Producto>> = productoDao.observarTodos()
    val movimientos: Flow<List<Movimiento>> = movimientoDao.observarTodos()

    fun observarProducto(id: Long): Flow<Producto?> = productoDao.observarPorId(id)

    fun observarMovimientosDe(productoId: Long): Flow<List<Movimiento>> =
        movimientoDao.observarPorProducto(productoId)

    suspend fun obtenerProducto(id: Long): Producto? = productoDao.obtenerPorId(id)

    // Descarga productos y movimientos del servidor y deja Room igual que el servidor.
    // Conserva la foto local de cada producto (el servidor no la guarda).
    suspend fun sincronizar(): Result<Unit> = llamar {
        val remotos = api.listarProductos()
        val fotos = productoDao.obtenerTodos().associate { it.id to it.fotoUri }
        productoDao.upsertTodos(remotos.map { it.aProducto(fotos[it.id]) })
        productoDao.eliminarQueNoEstan(remotos.map { it.id })

        val movs = api.listarMovimientos()
        movimientoDao.upsertTodos(movs.map { it.aMovimiento() })
        movimientoDao.eliminarQueNoEstan(movs.map { it.id })
    }

    // Crea el producto en el servidor. Si trae stock inicial, el servidor lo registra como un
    // movimiento de ENTRADA y aquí se descarga ese movimiento.
    suspend fun crearProducto(producto: Producto, stockInicial: Int): Result<Long> = llamar {
        val creado = api.crearProducto(producto.aRequest(stockInicial))
        productoDao.upsert(creado.aProducto(producto.fotoUri))
        if (stockInicial > 0) {
            movimientoDao.upsertTodos(api.movimientosDe(creado.id).map { it.aMovimiento() })
        }
        creado.id
    }.conDuplicado(producto.codigo)

    // Edita los datos. El stock NO se envía: solo cambia con movimientos.
    suspend fun guardarProducto(producto: Producto): Result<Long> = llamar {
        val editado = api.editarProducto(producto.id, producto.aRequest(0))
        productoDao.upsert(editado.aProducto(producto.fotoUri))
        editado.id
    }.conDuplicado(producto.codigo)

    suspend fun eliminarProducto(producto: Producto): Result<Unit> = llamar {
        api.eliminarProducto(producto.id)
        productoDao.eliminar(producto)
    }

    // El servidor valida (stock insuficiente = 422), guarda el movimiento y actualiza el stock
    // en una sola transacción. Aquí se copia el resultado a Room.
    suspend fun registrarMovimiento(
        productoId: Long,
        tipo: TipoMovimiento,
        cantidad: Int,
        nota: String = ""
    ): Result<Unit> = llamar {
        val creado = api.registrarMovimiento(productoId, MovimientoRequestDto(tipo.name, cantidad, nota))
        movimientoDao.upsert(creado.aMovimiento())
        productoDao.actualizarStock(productoId, creado.stockResultante)
    }

    // ---------- Internos ----------

    // Ejecuta una llamada al servidor y convierte los fallos en errores con mensaje para el usuario.
    private suspend fun <T> llamar(bloque: suspend () -> T): Result<T> = try {
        Result.success(bloque())
    } catch (e: HttpException) {
        Result.failure(traducir(e))
    } catch (e: IOException) {
        Result.failure(SinConexionException())
    } catch (e: JsonParseException) {
        Result.failure(ErrorServidorException(0, "Respuesta inesperada del servidor"))
    }

    private fun traducir(e: HttpException): ErrorServidorException {
        val texto = try {
            e.response()?.errorBody()?.string()
        } catch (ex: Exception) {
            null
        }
        val error = try {
            Gson().fromJson(texto, ErrorDto::class.java)
        } catch (ex: Exception) {
            null
        }
        return ErrorServidorException(
            codigo = e.code(),
            mensaje = error?.mensaje ?: "Error del servidor (${e.code()})",
            campos = error?.campos
        )
    }

    // 409 = código repetido: se devuelve la excepción que ya entienden los formularios.
    private fun <T> Result<T>.conDuplicado(codigo: String): Result<T> {
        val e = exceptionOrNull()
        return if (e is ErrorServidorException && e.codigo == 409) {
            Result.failure(CodigoDuplicadoException(codigo))
        } else {
            this
        }
    }
}
