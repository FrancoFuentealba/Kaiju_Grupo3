package com.example.kaiju_grupo3.model

import java.time.LocalDate
import java.time.format.DateTimeParseException

object ValidacionesProducto {

    const val PRECIO_MAXIMO = 10_000_000
    const val STOCK_MAXIMO = 1_000_000
    private val REGEX_CODIGO = Regex("^[A-Za-z0-9-]{3,20}$")

    fun validar(s: ProductoFormEstado): ProductoFormErrores = ProductoFormErrores(
        codigo = validarCodigo(s.codigo),
        nombre = validarTexto(s.nombre, minimo = 3, maximo = 60),
        descripcion = validarTextoOpcional(s.descripcion, maximo = 200),
        categoria = validarTexto(s.categoria, minimo = 2, maximo = 30),
        precio = validarEntero(s.precio, minimo = 1, maximo = PRECIO_MAXIMO),
        // El stock inicial solo existe al crear; al editar el stock cambia con movimientos.
        stockInicial = if (s.esNuevo) validarEntero(s.stockInicial, minimo = 0, maximo = STOCK_MAXIMO) else null,
        stockMinimo = validarEntero(s.stockMinimo, minimo = 0, maximo = STOCK_MAXIMO),
        talla = validarTextoOpcional(s.talla, maximo = 20),
        color = validarTextoOpcional(s.color, maximo = 20),
        modelo = validarTextoOpcional(s.modelo, maximo = 30),
        lote = validarTextoOpcional(s.lote, maximo = 20),
        vencimiento = validarFecha(s.vencimiento)
    )

    private fun validarCodigo(valor: String): String? {
        val limpio = valor.trim()
        return when {
            limpio.isEmpty() -> "Campo obligatorio"
            !REGEX_CODIGO.matches(limpio) -> "Usa 3 a 20 letras, números o guiones (ej: KJ-0012)"
            else -> null
        }
    }

    private fun validarTexto(valor: String, minimo: Int, maximo: Int): String? {
        val limpio = valor.trim()
        return when {
            limpio.isEmpty() -> "Campo obligatorio"
            limpio.length < minimo -> "Mínimo $minimo caracteres"
            limpio.length > maximo -> "Máximo $maximo caracteres"
            else -> null
        }
    }

    private fun validarTextoOpcional(valor: String, maximo: Int): String? =
        if (valor.trim().length > maximo) "Máximo $maximo caracteres" else null

    private fun validarEntero(valor: String, minimo: Int, maximo: Int): String? {
        val numero = valor.trim().toIntOrNull()
        return when {
            valor.isBlank() -> "Campo obligatorio"
            numero == null -> "Ingresa solo números enteros"
            numero < minimo -> "Debe ser al menos $minimo"
            numero > maximo -> "No puede superar $maximo"
            else -> null
        }
    }

    private fun validarFecha(valor: String): String? {
        if (valor.isBlank()) return null
        return try {
            LocalDate.parse(valor.trim())
            null
        } catch (e: DateTimeParseException) {
            "Fecha inválida. Usa el formato AAAA-MM-DD (ej: 2027-01-20)"
        }
    }
}
