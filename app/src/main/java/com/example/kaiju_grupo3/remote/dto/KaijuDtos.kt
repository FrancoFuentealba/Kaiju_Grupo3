package com.example.kaiju_grupo3.remote.dto

import com.example.kaiju_grupo3.model.Movimiento
import com.example.kaiju_grupo3.model.Producto
import com.example.kaiju_grupo3.model.TipoMovimiento

// Modelos de transferencia (DTO) del contrato de la API de Kaiju (ver API_KAIJU en el repo del backend).
// La foto NO viaja por la API: fotoUri solo existe en Room (es una ruta del teléfono).

// ---- Respuestas ----
data class ProductoDto(
    val id: Long,
    val codigo: String,
    val nombre: String,
    val descripcion: String?,
    val categoria: String,
    val tipo: String,
    val precio: Int,
    val stock: Int,
    val stockMinimo: Int,
    val talla: String?,
    val color: String?,
    val modelo: String?,
    val lote: String?,
    val vencimiento: String?
)

data class MovimientoDto(
    val id: Long,
    val productoId: Long,
    val tipo: String,
    val cantidad: Int,
    val stockResultante: Int,
    val fecha: Long,
    val nota: String?
)

// ---- Cuerpos de las peticiones ----
data class ProductoRequestDto(
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val tipo: String,
    val precio: Int,
    val stockMinimo: Int,
    val stockInicial: Int,      // solo lo usa el servidor al crear
    val talla: String?,
    val color: String?,
    val modelo: String?,
    val lote: String?,
    val vencimiento: String?
)

data class MovimientoRequestDto(
    val tipo: String,
    val cantidad: Int,
    val nota: String
)

// Cuerpo de los errores: { "mensaje": "...", "campos": { "codigo": "..." } }
data class ErrorDto(
    val mensaje: String?,
    val campos: Map<String, String>?
)

// ---- Conversiones entre DTO y las clases de Room ----

// fotoUri se conserva de la copia local (el servidor no la conoce).
fun ProductoDto.aProducto(fotoUri: String?): Producto = Producto(
    id = id,
    codigo = codigo,
    nombre = nombre,
    descripcion = descripcion.orEmpty(),
    categoria = categoria,
    tipo = tipo,
    precio = precio,
    stock = stock,
    stockMinimo = stockMinimo,
    talla = talla,
    color = color,
    modelo = modelo,
    lote = lote,
    vencimiento = vencimiento,
    fotoUri = fotoUri
)

fun MovimientoDto.aMovimiento(): Movimiento = Movimiento(
    id = id,
    productoId = productoId,
    tipo = TipoMovimiento.valueOf(tipo),
    cantidad = cantidad,
    stockResultante = stockResultante,
    fecha = fecha,
    nota = nota.orEmpty()
)

fun Producto.aRequest(stockInicial: Int): ProductoRequestDto = ProductoRequestDto(
    codigo = codigo,
    nombre = nombre,
    descripcion = descripcion,
    categoria = categoria,
    tipo = tipo,
    precio = precio,
    stockMinimo = stockMinimo,
    stockInicial = stockInicial,
    talla = talla,
    color = color,
    modelo = modelo,
    lote = lote,
    vencimiento = vencimiento
)
