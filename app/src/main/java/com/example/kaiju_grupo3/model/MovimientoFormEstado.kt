package com.example.kaiju_grupo3.model

// Estado del formulario para registrar un movimiento de stock.
data class MovimientoFormEstado(
    val producto: Producto? = null,          // producto al que se le registra el movimiento
    val tipo: TipoMovimiento = TipoMovimiento.ENTRADA,
    val cantidad: String = "",
    val nota: String = "",
    val guardando: Boolean = false,
    val errores: MovimientoFormErrores = MovimientoFormErrores()
) {
    // Stock que quedaría si se guardara ahora; null si la cantidad aún no es válida.
    // Sirve para mostrar una vista previa antes de confirmar.
    fun stockResultante(): Int? {
        val p = producto ?: return null
        val n = cantidad.trim().toIntOrNull() ?: return null
        if (ReglasStock.validarMovimiento(p.stock, tipo, n) != null) return null
        return ReglasStock.calcularStockResultante(p.stock, tipo, n)
    }
}
