package com.example.kaiju_grupo3.model

data class InventarioUiEstado (

    val productos: List<Producto> = emptyList(),
    val busqueda: String = "",
    val soloStockBajo: Boolean = false,
    val resumen: ResumenInventario = ResumenInventario(),
    val cargando: Boolean = true
)