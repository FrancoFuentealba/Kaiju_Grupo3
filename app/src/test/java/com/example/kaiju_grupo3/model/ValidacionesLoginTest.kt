package com.example.kaiju_grupo3.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidacionesLoginTest {

    @Test
    fun `campos validos no tienen errores`() {
        val errores = ValidacionesLogin.validar("prueba@kaiju.cl", "kaiju123")
        assertFalse(errores.hayErrores())
    }

    @Test
    fun `correo vacio es obligatorio`() {
        assertEquals("Campo obligatorio", ValidacionesLogin.validar("", "kaiju123").correo)
    }

    @Test
    fun `correo sin arroba es invalido`() {
        assertEquals("Correo inválido", ValidacionesLogin.validar("prueba.kaiju.cl", "kaiju123").correo)
    }

    @Test
    fun `clave corta es invalida`() {
        assertEquals("Debe tener al menos 6 caracteres", ValidacionesLogin.validar("a@b.cl", "123").clave)
    }

    @Test
    fun `clave vacia es obligatoria`() {
        assertEquals("Campo obligatorio", ValidacionesLogin.validar("a@b.cl", "").clave)
    }

    @Test
    fun `usuario de prueba puede entrar`() {
        assertTrue(ValidacionesLogin.credencialesValidas("PRUEBA@kaiju.cl", "kaiju123", null))
    }

    @Test
    fun `clave incorrecta del usuario de prueba no entra`() {
        assertFalse(ValidacionesLogin.credencialesValidas("prueba@kaiju.cl", "otraClave", null))
    }

    @Test
    fun `usuario registrado puede entrar`() {
        val registrado = UsuarioUiEstado(correo = "ana@correo.cl", clave = "secreto1")
        assertTrue(ValidacionesLogin.credencialesValidas("ana@correo.cl", "secreto1", registrado))
    }

    @Test
    fun `usuario desconocido no entra`() {
        val registrado = UsuarioUiEstado(correo = "ana@correo.cl", clave = "secreto1")
        assertFalse(ValidacionesLogin.credencialesValidas("otro@correo.cl", "secreto1", registrado))
    }

    @Test
    fun `registro vacio no permite entrar con campos vacios`() {
        assertFalse(ValidacionesLogin.credencialesValidas("", "", UsuarioUiEstado()))
    }

    @Test
    fun `errores nulos significa sin errores`() {
        assertNull(LoginErrores().correo)
        assertNotNull(LoginErrores(general = "x").general)
    }
}