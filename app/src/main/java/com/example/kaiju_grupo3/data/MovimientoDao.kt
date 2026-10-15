package com.example.kaiju_grupo3.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.kaiju_grupo3.model.Movimiento
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoDao {

    @Insert
    suspend fun insertar(movimiento: Movimiento): Long

    @Query("SELECT * FROM movimientos ORDER BY fecha DESC, id DESC")
    fun observarTodos(): Flow<List<Movimiento>>

    @Query("SELECT * FROM movimientos WHERE productoId = :productoId ORDER BY fecha DESC, id DESC")
    fun observarPorProducto(productoId: Long): Flow<List<Movimiento>>
}
