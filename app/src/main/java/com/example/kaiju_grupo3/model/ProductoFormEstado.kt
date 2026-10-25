package com.example.kaiju_grupo3.model

val TIPOS_PRODUCTO = listOf("Producto terminado", "Insumo", "Perecible")

data class ProductoFormEstado(
    val id: Long = 0,
    val codigo: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val tipo: String = TIPOS_PRODUCTO.first(),
    val precio: String = "",
    val stockInicial: String = "0",         // solo se usa al crear
    val stockMinimo: String = "",
    val talla: String = "",
    val color: String = "",
    val modelo: String = "",
    val lote: String = "",
    val vencimiento: String = "",
    val fotoUri: String? = null,
    val cargando: Boolean = false,          // leyendo el producto a editar
    val guardando: Boolean = false,         // guardando en la base de datos
    val errores: ProductoFormErrores = ProductoFormErrores()
) {
    val esNuevo: Boolean get() = id == 0L
}

