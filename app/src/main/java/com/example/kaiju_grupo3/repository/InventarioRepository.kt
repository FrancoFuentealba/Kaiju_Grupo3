package com.example.kaiju_grupo3.repository

import androidx.room.withTransaction
import com.example.kaiju_grupo3.data.DatosEjemplo
import com.example.kaiju_grupo3.data.KaijuDatabase
import com.example.kaiju_grupo3.model.Movimiento
import com.example.kaiju_grupo3.model.Producto
import com.example.kaiju_grupo3.model.ReglasStock
import com.example.kaiju_grupo3.model.TipoMovimiento
import kotlinx.coroutines.flow.Flow

class CodigoDuplicadoException(codigo: String) :
    Exception("Ya existe un producto con el código $codigo")

class InventarioRepository(private val db: KaijuDatabase) {

    private val productoDao = db.productoDao()
    private val movimientoDao = db.movimientoDao()

    val productos: Flow<List<Producto>> = productoDao.observarTodos()
    val movimientos: Flow<List<Movimiento>> = movimientoDao.observarTodos()

    fun observarProducto(id: Long): Flow<Producto?> = productoDao.observarPorId(id)

    fun observarMovimientosDe(productoId: Long): Flow<List<Movimiento>> =
        movimientoDao.observarPorProducto(productoId)

    suspend fun obtenerProducto(id: Long): Producto? = productoDao.obtenerPorId(id)

    // Esto trae stock inicial, lo registra como un movimiento de ENTRADA
    suspend fun crearProducto(producto: Producto, stockInicial: Int): Result<Long> = runCatching {
        db.withTransaction {
            if (productoDao.contarPorCodigo(producto.codigo, 0) > 0) {
                throw CodigoDuplicadoException(producto.codigo)
            }
            val id = productoDao.insertar(producto.copy(id = 0, stock = 0))
            if (stockInicial > 0) {
                registrarMovimiento(id, TipoMovimiento.ENTRADA, stockInicial, "Stock inicial").getOrThrow()
            }
            id
        }
    }


    suspend fun guardarProducto(producto: Producto): Result<Long> = runCatching {
        db.withTransaction {
            if (productoDao.contarPorCodigo(producto.codigo, producto.id) > 0) {
                throw CodigoDuplicadoException(producto.codigo)
            }
            val actual = productoDao.obtenerPorId(producto.id)
                ?: throw IllegalArgumentException("El producto no existe")
            productoDao.actualizar(producto.copy(stock = actual.stock))
            producto.id
        }
    }

    suspend fun eliminarProducto(producto: Producto) = productoDao.eliminar(producto)

    suspend fun registrarMovimiento(
        productoId: Long,
        tipo: TipoMovimiento,
        cantidad: Int,
        nota: String = "",
        fecha: Long = System.currentTimeMillis()
    ): Result<Unit> = db.withTransaction {
        val producto = productoDao.obtenerPorId(productoId)
            ?: return@withTransaction Result.failure(IllegalArgumentException("El producto no existe"))

        val error = ReglasStock.validarMovimiento(producto.stock, tipo, cantidad)
        if (error != null) {
            return@withTransaction Result.failure(IllegalArgumentException(error))
        }

        val nuevoStock = ReglasStock.calcularStockResultante(producto.stock, tipo, cantidad)
        movimientoDao.insertar(
            Movimiento(
                productoId = productoId,
                tipo = tipo,
                cantidad = cantidad,
                stockResultante = nuevoStock,
                fecha = fecha,
                nota = nota
            )
        )
        productoDao.actualizarStock(productoId, nuevoStock)
        Result.success(Unit)
    }

    suspend fun sembrarDatosDeEjemploSiVacio() {
        if (productoDao.contar() > 0) return
        val ahora = System.currentTimeMillis()
        val dia = 24L * 60 * 60 * 1000
        db.withTransaction {
            DatosEjemplo.catalogo().forEach { ejemplo ->
                val id = productoDao.insertar(ejemplo.producto)
                ejemplo.movimientos.forEach { m ->
                    registrarMovimiento(
                        productoId = id,
                        tipo = m.tipo,
                        cantidad = m.cantidad,
                        nota = m.nota,
                        fecha = ahora - m.diasAtras * dia
                    )
                }
            }
        }
    }
}
