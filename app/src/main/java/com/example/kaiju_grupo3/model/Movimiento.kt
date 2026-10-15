package com.example.kaiju_grupo3.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Tabla "movimientos": historial de cada cambio de stock.
// Si se borra un producto, se borran también sus movimientos.
@Entity(
    tableName = "movimientos",
    foreignKeys = [
        ForeignKey(
            entity = Producto::class,
            parentColumns = ["id"],
            childColumns = ["productoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["productoId"])]
)
data class Movimiento(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productoId: Long,
    val tipo: TipoMovimiento,
    val cantidad: Int,          // siempre positiva; en un AJUSTE es el nuevo stock contado
    val stockResultante: Int,   // stock que quedó después del movimiento
    val fecha: Long = System.currentTimeMillis(),
    val nota: String = ""
)
