package com.example.kaiju_grupo3.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.kaiju_grupo3.model.Producto
import kotlinx.coroutines.flow.Flow
@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos ORDER BY nombre COLLATE NOCASE")
    fun observarTodos(): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE id = :id")
    fun observarPorId(id: Long): Flow<Producto?>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Producto?

    @Query("SELECT COUNT(*) FROM productos")
    suspend fun contar(): Int

    // Cuenta productos con ese código, ignorando el producto que se está editando.
    @Query("SELECT COUNT(*) FROM productos WHERE codigo = :codigo AND id != :excluirId")
    suspend fun contarPorCodigo(codigo: String, excluirId: Long): Int

    @Insert
    suspend fun insertar(producto: Producto): Long

    // Inserta o actualiza SIN borrar la fila (así no se pierden los movimientos por el ON DELETE CASCADE).
    @Upsert
    suspend fun upsert(producto: Producto)

    @Upsert
    suspend fun upsertTodos(productos: List<Producto>)

    @Query("SELECT * FROM productos")
    suspend fun obtenerTodos(): List<Producto>

    // Borra los productos que el servidor ya no tiene.
    @Query("DELETE FROM productos WHERE id NOT IN (:ids)")
    suspend fun eliminarQueNoEstan(ids: List<Long>)

    @Update
    suspend fun actualizar(producto: Producto)

    @Delete
    suspend fun eliminar(producto: Producto)

    @Query("UPDATE productos SET stock = :stock WHERE id = :id")
    suspend fun actualizarStock(id: Long, stock: Int)
}