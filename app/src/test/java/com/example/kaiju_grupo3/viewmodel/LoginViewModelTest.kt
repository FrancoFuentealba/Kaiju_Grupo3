package com.example.kaiju_grupo3.viewmodel

import com.example.kaiju_grupo3.model.UsuarioUiEstado
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginViewModelTest {

    private lateinit var vm: LoginViewModel

    @Before
    fun setUp() {
        vm = LoginViewModel()
    }

    @Test
    fun `el estado inicial esta vacio y sin errores`() {
        val s = vm.estado.value
        assertEquals("", s.correo)
        assertEquals("", s.clave)
        assertFalse(s.errores.hayErrores())
    }

    @Test
    fun `onCorreoChange y onClaveChange guardan lo que escribe el usuario`() {
        vm.onCorreoChange("a@b.cl")
        vm.onClaveChange("123456")

        assertEquals("a@b.cl", vm.estado.value.correo)
        assertEquals("123456", vm.estado.value.clave)
    }

    @Test
    fun `iniciarSesion con campos vacios devuelve false y marca los errores`() {
        val entro = vm.iniciarSesion(null)

        assertFalse(entro)
        assertEquals("Campo obligatorio", vm.estado.value.errores.correo)
        assertEquals("Campo obligatorio", vm.estado.value.errores.clave)
    }

    @Test
    fun `escribir en un campo borra su error`() {
        vm.iniciarSesion(null)

        vm.onCorreoChange("a")

        assertNull(vm.estado.value.errores.correo)
        assertEquals("Campo obligatorio", vm.estado.value.errores.clave)
    }

    @Test
    fun `credenciales incorrectas muestran el error general`() {
        vm.onCorreoChange("otro@kaiju.cl")
        vm.onClaveChange("claveMala1")

        val entro = vm.iniciarSesion(null)

        assertFalse(entro)
        assertEquals("Correo o contraseña incorrectos", vm.estado.value.errores.general)
    }

    @Test
    fun `el usuario de prueba puede entrar`() {
        vm.onCorreoChange("prueba@kaiju.cl")
        vm.onClaveChange("kaiju123")

        assertTrue(vm.iniciarSesion(null))
        assertFalse(vm.estado.value.errores.hayErrores())
    }

    @Test
    fun `el usuario recien registrado puede entrar`() {
        val registrado = UsuarioUiEstado(correo = "nuevo@kaiju.cl", clave = "secreta99")
        vm.onCorreoChange("nuevo@kaiju.cl")
        vm.onClaveChange("secreta99")

        assertTrue(vm.iniciarSesion(registrado))
    }

    @Test
    fun `cambiar el correo borra el error general`() {
        vm.onCorreoChange("otro@kaiju.cl")
        vm.onClaveChange("claveMala1")
        vm.iniciarSesion(null)

        vm.onCorreoChange("prueba@kaiju.cl")

        assertNull(vm.estado.value.errores.general)
    }
}