package com.example.kaiju_grupo3.model

// Números resumen que se muestran arriba de la lista de inventario.
data class ResumenInventario(
    val totalProductos: Int = 0,
    val totalUnidades: Int = 0,
    val productosStockBajo: Int = 0
)
