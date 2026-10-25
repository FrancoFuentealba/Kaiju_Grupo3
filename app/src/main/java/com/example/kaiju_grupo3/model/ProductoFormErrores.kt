package com.example.kaiju_grupo3.model

// Un mensaje de error por campo del formulario de producto.
data class ProductoFormErrores(
    val codigo: String? = null,
    val nombre: String? = null,
    val descripcion: String? = null,
    val categoria: String? = null,
    val precio: String? = null,
    val stockInicial: String? = null,
    val stockMinimo: String? = null,
    val talla: String? = null,
    val color: String? = null,
    val modelo: String? = null,
    val lote: String? = null,
    val vencimiento: String? = null,
    val general: String? = null     // errores que no pertenecen a un campo en concreto
) {
    fun hayErrores(): Boolean = listOfNotNull(
        codigo, nombre, descripcion, categoria, precio, stockInicial, stockMinimo,
        talla, color, modelo, lote, vencimiento, general
    ).isNotEmpty()
}
