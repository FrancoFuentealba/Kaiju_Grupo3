package com.example.kaiju_grupo3.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Tabla "productos" de la base de datos local.
// El código es único, no pueden existir dos productos con el mismo código.
@Entity(
    tableName = "productos",
    indices = [Index(value = ["codigo"], unique = true)]
)
data class Producto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val codigo: String,
    val nombre: String,
    val descripcion: String = "",
    val categoria: String,
    val tipo: String,                 // tipo de producto,  terminado, insumo, perecible...
    val precio: Int,                  // en pesos chilenos
    val stock: Int = 0,               // cantidad disponible
    val stockMinimo: Int = 0,         // bajo este nivel se alerta reposición
    // Atributos opcionales según el tipo de producto
    val talla: String? = null,
    val color: String? = null,
    val modelo: String? = null,
    val lote: String? = null,
    val vencimiento: String? = null,
    val fotoUri: String? = null       // foto tomada con la cámara
)

// Un producto tiene stock bajo cuando llega por debajo de su stock mínimo.
val Producto.stockBajo: Boolean
    get() = stock <= stockMinimo