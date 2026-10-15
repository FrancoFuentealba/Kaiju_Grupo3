package com.example.kaiju_grupo3.repository

import androidx.room.withTransaction
import com.example.kaiju_grupo3.data.DatosEjemplo
import com.example.kaiju_grupo3.data.KaijuDatabase
import com.example.kaiju_grupo3.model.Movimiento
import com.example.kaiju_grupo3.model.Producto
import com.example.kaiju_grupo3.model.ReglasStock
import com.example.kaiju_grupo3.model.TipoMovimiento
import kotlinx.coroutines.flow.Flow

// Repositorio: único punto de acceso a los datos del inventario.
// Los ViewModels hablan con esta clase.
class InventarioRepository(private val db: KaijuDatabase) {

    private val productoDao = db.productoDao()
    private val movimientoDao = db.movimientoDao()

    val productos: Flow<List<Producto>> = productoDao.observarTodos()
    val movimientos: Flow<List<Movimiento>> = movimientoDao.observarTodos()

    fun observarProducto(id: Long): Flow<Producto?> = productoDao.observarPorId(id)

    fun observarMovimientosDe(productoId: Long): Flow<List<Movimiento>> =
        movimientoDao.observarPorProducto(productoId)

    // Crea o edita un producto (id == 0 significa producto nuevo). Rechaza códigos repetidos.
    // OJO: el stock solo debe cambiar mediante registrarMovimiento, para no perder trazabilidad.
    suspend fun guardarProducto(producto: Producto): Result<Long> {
        if (productoDao.contarPorCodigo(producto.codigo, producto.id) > 0) {
            return Result.failure(IllegalArgumentException("Ya existe un producto con el código ${producto.codigo}"))
        }
        return if (producto.id == 0L) {
            Result.success(productoDao.insertar(producto))
        } else {
            productoDao.actualizar(producto)
            Result.success(producto.id)
        }
    }

    suspend fun eliminarProducto(producto: Producto) = productoDao.eliminar(producto)

    // Registra un movimiento y actualiza el stock del producto en UNA sola transacción:
    // o se guardan las dos cosas, o no se guarda ninguna.
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

    // Carga el catálogo ficticio la primera vez que se abre la app (base de datos vacía).
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
