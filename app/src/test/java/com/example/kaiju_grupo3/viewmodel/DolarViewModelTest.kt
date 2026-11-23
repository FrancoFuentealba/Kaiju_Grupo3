package com.example.kaiju_grupo3.viewmodel

import com.example.kaiju_grupo3.MainDispatcherRule
import com.example.kaiju_grupo3.model.ValorIndicador
import com.example.kaiju_grupo3.repository.DolarRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class DolarViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val repo = mockk<DolarRepository>()
    private val valor = ValorIndicador("2026-11-20T03:00:00.000Z", 1008.79)

    @Test
    fun `al crearse carga el dolar y deja el valor en el estado`() = runTest {
        coEvery { repo.obtenerDolarHoy() } returns valor

        val vm = DolarViewModel(repo)

        assertEquals(valor, vm.dolar.value)
        assertNull(vm.error.value)
        assertFalse(vm.cargando.value)
    }

    @Test
    fun `si no hay valor disponible muestra un mensaje`() = runTest {
        coEvery { repo.obtenerDolarHoy() } returns null

        val vm = DolarViewModel(repo)

        assertNull(vm.dolar.value)
        assertEquals("No hay valor del dólar disponible", vm.error.value)
        assertFalse(vm.cargando.value)
    }

    @Test
    fun `si la API falla muestra el error y termina de cargar`() = runTest {
        coEvery { repo.obtenerDolarHoy() } throws IOException("sin red")

        val vm = DolarViewModel(repo)

        assertNull(vm.dolar.value)
        assertEquals("No se pudo obtener el precio del dólar", vm.error.value)
        assertFalse(vm.cargando.value)
    }

    @Test
    fun `cargarDolar reintenta y limpia el error cuando ahora funciona`() = runTest {
        coEvery { repo.obtenerDolarHoy() } throws IOException("sin red")
        val vm = DolarViewModel(repo)
        coEvery { repo.obtenerDolarHoy() } returns valor

        vm.cargarDolar()

        assertEquals(valor, vm.dolar.value)
        assertNull(vm.error.value)
        coVerify(exactly = 2) { repo.obtenerDolarHoy() }
    }
}