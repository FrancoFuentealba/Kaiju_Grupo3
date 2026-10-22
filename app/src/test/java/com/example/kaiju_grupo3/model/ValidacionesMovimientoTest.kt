package com.example.kaiju_grupo3.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertEquals
import org.junit.Test

class ValidacionesMovimientoTest {

    private val producto = Producto(
        id = 1, codigo = "KJ-0001", nombre = "Polera", categoria = "Ropa",
        tipo = "Producto terminado", precio = 9990, stock = 10, stockMinimo = 3
    )

    @Test
    fun entrada_valida_no_da_errores() {
        val errores = ValidacionesMovimiento.validar(producto, TipoMovimiento.ENTRADA, "5", "")
        assertFalse(errores.hayErrores())
    }

    @Test
    fun cantidad_vacia_o_no_numerica_da_error() {
        assertNotNull(ValidacionesMovimiento.validar(producto, TipoMovimiento.ENTRADA, "", "").cantidad)
        assertNotNull(ValidacionesMovimiento.validar(producto, TipoMovimiento.ENTRADA, "abc", "").cantidad)
    }

    @Test
    fun salida_no_puede_superar_el_stock() {
        assertNotNull(ValidacionesMovimiento.validar(producto, TipoMovimiento.SALIDA, "11", "").cantidad)
        assertNull(ValidacionesMovimiento.validar(producto, TipoMovimiento.SALIDA, "10", "").cantidad)
    }

    @Test
    fun cantidad_cero_solo_vale_en_ajuste() {
        assertNotNull(ValidacionesMovimiento.validar(producto, TipoMovimiento.VENTA, "0", "").cantidad)
        assertNull(ValidacionesMovimiento.validar(producto, TipoMovimiento.AJUSTE, "0", "").cantidad)
    }

    @Test
    fun nota_larga_da_error() {
        val errores = ValidacionesMovimiento.validar(producto, TipoMovimiento.ENTRADA, "1", "x".repeat(121))
        assertNotNull(errores.nota)
    }

    @Test
    fun sin_producto_da_error_general() {
        assertNotNull(ValidacionesMovimiento.validar(null, TipoMovimiento.ENTRADA, "1", "").general)
    }

    @Test
    fun vista_previa_del_stock_resultante() {
        val estado = MovimientoFormEstado(producto = producto, tipo = TipoMovimiento.VENTA, cantidad = "4")
        assertEquals(6, estado.stockResultante())
        // Si la cantidad no es válida no hay vista previa
        assertNull(estado.copy(cantidad = "99").stockResultante())
    }
}
