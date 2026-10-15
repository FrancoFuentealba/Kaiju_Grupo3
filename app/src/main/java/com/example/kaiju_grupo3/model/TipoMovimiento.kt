package com.example.kaiju_grupo3.model

// Tipos de movimiento que puede tener un producto en el inventario.
enum class TipoMovimiento(val etiqueta: String) {
    ENTRADA("Entrada"),   // llegan unidades
    SALIDA("Salida"),     // salen unidades sin venta (merma)
    VENTA("Venta"),       // salen unidades por una venta
    AJUSTE("Ajuste")      // se corrige el stock al conteo físico real
}
