package com.example.kaiju_grupo3.model
object ValidacionesMovimiento {
    const val CANTIDAD_MAXIMA = 1_000_000
    fun validar(
        producto: Producto?,
        tipo: TipoMovimiento,
        cantidad: String,
        nota: String
    ): MovimientoFormErrores {
        if (producto == null) {
            return MovimientoFormErrores(general = "No se encontró el producto")
        }
        return MovimientoFormErrores(
            cantidad = validarCantidad(producto, tipo, cantidad),
            nota = if (nota.trim().length > 120) "Máximo 120 caracteres" else null
        )
    }
    private fun validarCantidad(producto: Producto, tipo: TipoMovimiento, texto: String): String? {
        val numero = texto.trim().toIntOrNull()
        return when {
            texto.isBlank() -> if (tipo == TipoMovimiento.AJUSTE) "Ingresa el stock contado" else "Ingresa la cantidad"
            numero == null -> "Ingresa solo números enteros"
            numero > CANTIDAD_MAXIMA -> "No puede superar $CANTIDAD_MAXIMA"
            else -> ReglasStock.validarMovimiento(producto.stock, tipo, numero)
        }
    }
}
