package com.example.kaiju_grupo3.repository

import com.example.kaiju_grupo3.model.IndicadorResponse
import com.example.kaiju_grupo3.model.ValorIndicador
import com.example.kaiju_grupo3.remote.MindicadorApi
import com.example.kaiju_grupo3.remote.MindicadorClient
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class DolarRepositoryTest {

    private val api = mockk<MindicadorApi>()
    private val repo = DolarRepository()

    @Before
    fun setUp() {
        // Se reemplaza la API real de mindicador.cl por un mock.
        mockkObject(MindicadorClient)
        every { MindicadorClient.api } returns api
    }

    @After
    fun tearDown() {
        unmockkObject(MindicadorClient)
    }

    private fun respuesta(vararg serie: ValorIndicador) = IndicadorResponse(
        codigo = "dolar", nombre = "Dólar observado", unidadMedida = "Pesos", serie = serie.toList()
    )

    @Test
    fun `obtenerDolarHoy devuelve el valor de la fecha mas reciente`() = runTest {
        coEvery { api.getDolar() } returns respuesta(
            ValorIndicador("2026-11-18T03:00:00.000Z", 1001.5),
            ValorIndicador("2026-11-20T03:00:00.000Z", 1008.79),
            ValorIndicador("2026-11-19T03:00:00.000Z", 1005.0)
        )

        val hoy = repo.obtenerDolarHoy()

        assertEquals(1008.79, hoy?.valor ?: 0.0, 0.001)
        assertEquals("2026-11-20T03:00:00.000Z", hoy?.fecha)
    }

    @Test
    fun `obtenerDolarHoy con serie vacia devuelve null`() = runTest {
        coEvery { api.getDolar() } returns respuesta()

        assertNull(repo.obtenerDolarHoy())
    }
}