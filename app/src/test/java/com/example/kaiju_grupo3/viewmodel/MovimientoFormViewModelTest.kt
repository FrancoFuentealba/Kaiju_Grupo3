package com.example.kaiju_grupo3.viewmodel

import android.app.Application
import com.example.kaiju_grupo3.MainDispatcherRule
import com.example.kaiju_grupo3.model.Producto
import com.example.kaiju_grupo3.model.TipoMovimiento
import com.example.kaiju_grupo3.repository.InventarioRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovimientoFormViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val app = mockk<Application>(relaxed = true)
    private val repo = mockk<InventarioRepository>()
    private lateinit var vm: MovimientoFormViewModel

    private val polera = Producto(
        id = 1, codigo = "KJ-0001", nombre = "Polera", categoria = "Ropa",
        tipo = "Producto terminado", precio = 9990, stock = 10, stockMinimo = 2
    )

    @Before
    fun setUp() {
        every { repo.observarProducto(1) } returns flowOf(polera)
        vm = MovimientoFormViewModel(app, repo)
    }

    @Test
    fun `cargarProducto deja el producto en el estado`() = runTest {
        vm.cargarProducto(1)

        assertEquals(polera, vm.estado.value.producto)
    }

    @Test
    fun `cargarProducto con el mismo id no vuelve a observar`() = runTest {
        vm.cargarProducto(1)
        vm.cargarProducto(1)

        io.mockk.verify(exactly = 1) { repo.observarProducto(1) }
    }

    @Test
    fun `la cantidad solo acepta digitos y maximo 7`() {
        vm.onCantidadChange("12ab34567890")

        assertEquals("1234567", vm.estado.value.cantidad)
    }

    @Test
    fun `cambiar el tipo y la nota actualiza el estado`() {
        vm.onTipoChange(TipoMovimiento.SALIDA)
        vm.onNotaChange("venta mostrador")

        assertEquals(TipoMovimiento.SALIDA, vm.estado.value.tipo)
        assertEquals("venta mostrador", vm.estado.value.nota)
    }

    @Test
    fun `cantidad vacia no es valida`() = runTest {
        vm.cargarProducto(1)

        assertFalse(vm.validarFormulario())
        assertEquals("Ingresa la cantidad", vm.estado.value.errores.cantidad)
    }

    @Test
    fun `una salida mayor al stock no es valida`() = runTest {
        vm.cargarProducto(1)
        vm.onTipoChange(TipoMovimiento.SALIDA)
        vm.onCantidadChange("50")

        assertFalse(vm.validarFormulario())
        assertEquals("Stock insuficiente: hay 10 unidades disponibles", vm.estado.value.errores.cantidad)
    }

    @Test
    fun `sin producto cargado el formulario no es valido`() {
        vm.onCantidadChange("3")

        assertFalse(vm.validarFormulario())
        assertEquals("No se encontró el producto", vm.estado.value.errores.general)
    }

    @Test
    fun `cambiar la cantidad borra su error`() = runTest {
        vm.cargarProducto(1)
        vm.validarFormulario()

        vm.onCantidadChange("2")

        assertNull(vm.estado.value.errores.cantidad)
    }

    @Test
    fun `stockResultante calcula como quedaria el stock`() = runTest {
        vm.cargarProducto(1)
        vm.onTipoChange(TipoMovimiento.ENTRADA)
        vm.onCantidadChange("5")

        assertEquals(15, vm.estado.value.stockResultante())
    }

    @Test
    fun `guardar un movimiento valido llama al repositorio y a onExito`() = runTest {
        coEvery { repo.registrarMovimiento(any(), any(), any(), any()) } returns Result.success(Unit)
        vm.cargarProducto(1)
        vm.onTipoChange(TipoMovimiento.SALIDA)
        vm.onCantidadChange("3")
        vm.onNotaChange("  venta  ")
        var exito = false

        vm.guardar { exito = true }

        assertTrue(exito)
        coVerify { repo.registrarMovimiento(1, TipoMovimiento.SALIDA, 3, "venta") }
    }

    @Test
    fun `guardar con datos invalidos no llama al repositorio`() = runTest {
        vm.cargarProducto(1)
        var exito = false

        vm.guardar { exito = true }

        assertFalse(exito)
        coVerify(exactly = 0) { repo.registrarMovimiento(any(), any(), any(), any()) }
    }

    @Test
    fun `si el servidor rechaza el movimiento se muestra su mensaje`() = runTest {
        coEvery { repo.registrarMovimiento(any(), any(), any(), any()) } returns
                Result.failure(Exception("Stock insuficiente: hay 3 unidades disponibles"))
        vm.cargarProducto(1)
        vm.onCantidadChange("2")

        vm.guardar { }

        assertEquals("Stock insuficiente: hay 3 unidades disponibles", vm.estado.value.errores.general)
        assertFalse(vm.estado.value.guardando)
    }
}