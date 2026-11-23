package com.example.kaiju_grupo3.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// validarFormulario() usa android.util.Patterns, que no existe en un test unitario de la JVM;
// esa función se prueba en androidTest. Aquí se prueba el resto del estado del formulario.
class UsuarioViewModelTest {

    private lateinit var vm: UsuarioViewModel

    @Before
    fun setUp() {
        vm = UsuarioViewModel()
    }

    @Test
    fun `el estado inicial esta vacio y sin terminos aceptados`() {
        val s = vm.estado.value
        assertEquals("", s.nombre)
        assertEquals("", s.correo)
        assertEquals("", s.clave)
        assertEquals("", s.direccion)
        assertFalse(s.aceptaTerminos)
    }

    @Test
    fun `cada onChange guarda el valor en su campo`() {
        vm.onNombreChange("Franco")
        vm.onCorreoChange("franco@kaiju.cl")
        vm.onClaveChange("kaiju123")
        vm.onDireccionChange("Santiago")
        vm.onAceptarTerminosChange(true)

        val s = vm.estado.value
        assertEquals("Franco", s.nombre)
        assertEquals("franco@kaiju.cl", s.correo)
        assertEquals("kaiju123", s.clave)
        assertEquals("Santiago", s.direccion)
        assertTrue(s.aceptaTerminos)
    }

    @Test
    fun `cambiar un campo no modifica los demas`() {
        vm.onNombreChange("Franco")
        vm.onDireccionChange("Santiago")

        vm.onNombreChange("Franco F.")

        assertEquals("Franco F.", vm.estado.value.nombre)
        assertEquals("Santiago", vm.estado.value.direccion)
    }
}