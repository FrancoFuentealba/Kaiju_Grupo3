package com.example.kaiju_grupo3.model

// Reglas de negocio del stock, separadas de Room y de la UI para probarlas en tests simples.

object ReglasStock {

    // Calcula cuánto stock queda después de aplicar un movimiento.
    fun calcularStockResultante(stockActual: Int, tipo: TipoMovimiento, cantidad: Int): Int =
        when (tipo) {
            TipoMovimiento.ENTRADA -> stockActual + cantidad
            TipoMovimiento.SALIDA, TipoMovimiento.VENTA -> stockActual - cantidad
            TipoMovimiento.AJUSTE -> cantidad //  ajuste fija el stock al conteo real
        }

    // Devuelve un mensaje de error si el movimiento no se puede registrar, o null si es válido.
    fun validarMovimiento(stockActual: Int, tipo: TipoMovimiento, cantidad: Int): String? {
        if (tipo == TipoMovimiento.AJUSTE) {
            return if (cantidad < 0) "El stock contado no puede ser negativo" else null
        }
        if (cantidad <= 0) return "La cantidad debe ser mayor a 0"
        val resultante = calcularStockResultante(stockActual, tipo, cantidad)
        return if (resultante < 0) "Stock insuficiente: hay $stockActual unidades disponibles" else null
    }
}
