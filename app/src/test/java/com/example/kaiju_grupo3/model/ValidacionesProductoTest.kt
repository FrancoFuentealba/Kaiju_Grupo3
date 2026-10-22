package com.example.kaiju_grupo3.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidacionesProductoTest {

    // Formulario válido de base; cada test cambia un solo campo.
    private fun formularioValido() = ProductoFormEstado(
        codigo = "KJ-0012",
        nombre = "Polera básica",
        categoria = "Ropa",
        precio = "9990",
        stockInicial = "10",
        stockMinimo = "3"
    )

    @Test
    fun formulario_valido_no_tiene_errores() {
        assertFalse(ValidacionesProducto.validar(formularioValido()).hayErrores())
    }

    @Test
    fun campos_obligatorios_vacios_dan_error() {
        val errores = ValidacionesProducto.validar(ProductoFormEstado())
        assertNotNull(errores.codigo)
        assertNotNull(errores.nombre)
        assertNotNull(errores.categoria)
        assertNotNull(errores.precio)
        assertNotNull(errores.stockMinimo)
        assertTrue(errores.hayErrores())
    }

    @Test
    fun codigo_rechaza_espacios_y_simbolos() {
        assertNotNull(ValidacionesProducto.validar(formularioValido().copy(codigo = "KJ 01")).codigo)
        assertNotNull(ValidacionesProducto.validar(formularioValido().copy(codigo = "K!")).codigo)
        assertNull(ValidacionesProducto.validar(formularioValido().copy(codigo = "ABC-123")).codigo)
    }

    @Test
    fun nombre_necesita_minimo_3_caracteres() {
        assertNotNull(ValidacionesProducto.validar(formularioValido().copy(nombre = "ab")).nombre)
        assertNotNull(ValidacionesProducto.validar(formularioValido().copy(nombre = "   ")).nombre)
    }

    @Test
    fun precio_debe_ser_numero_mayor_a_cero() {
        assertNotNull(ValidacionesProducto.validar(formularioValido().copy(precio = "0")).precio)
        assertNotNull(ValidacionesProducto.validar(formularioValido().copy(precio = "abc")).precio)
        assertNotNull(ValidacionesProducto.validar(formularioValido().copy(precio = "99999999")).precio)
        assertNull(ValidacionesProducto.validar(formularioValido().copy(precio = "1")).precio)
    }

    @Test
    fun stock_minimo_acepta_cero_pero_no_negativos() {
        assertNull(ValidacionesProducto.validar(formularioValido().copy(stockMinimo = "0")).stockMinimo)
        assertNotNull(ValidacionesProducto.validar(formularioValido().copy(stockMinimo = "-1")).stockMinimo)
    }

    @Test
    fun stock_inicial_solo_se_valida_al_crear() {
        val nuevoInvalido = formularioValido().copy(stockInicial = "")
        assertNotNull(ValidacionesProducto.validar(nuevoInvalido).stockInicial)
        // Al editar (id != 0) el campo no existe, por lo tanto no se valida.
        val edicion = nuevoInvalido.copy(id = 5)
        assertNull(ValidacionesProducto.validar(edicion).stockInicial)
    }

    @Test
    fun vencimiento_es_opcional_pero_si_se_escribe_debe_ser_fecha_real() {
        assertNull(ValidacionesProducto.validar(formularioValido().copy(vencimiento = "")).vencimiento)
        assertNull(ValidacionesProducto.validar(formularioValido().copy(vencimiento = "2027-01-20")).vencimiento)
        assertNotNull(ValidacionesProducto.validar(formularioValido().copy(vencimiento = "20/01/2027")).vencimiento)
        assertNotNull(ValidacionesProducto.validar(formularioValido().copy(vencimiento = "2027-02-30")).vencimiento)
    }

    @Test
    fun descripcion_larga_da_error() {
        val larga = "x".repeat(201)
        assertEquals("Máximo 200 caracteres", ValidacionesProducto.validar(formularioValido().copy(descripcion = larga)).descripcion)
    }
}