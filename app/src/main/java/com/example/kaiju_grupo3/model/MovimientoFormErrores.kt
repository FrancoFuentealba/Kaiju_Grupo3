package com.example.kaiju_grupo3.model

data class MovimientoFormErrores(
    val cantidad: String? = null,
    val nota: String? = null,
    val general: String? = null
) {
    fun hayErrores(): Boolean = listOfNotNull(cantidad, nota, general).isNotEmpty()
}
