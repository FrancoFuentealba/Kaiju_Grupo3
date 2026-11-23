package com.example.kaiju_grupo3.viewmodel

import android.app.Application
import com.example.kaiju_grupo3.MainDispatcherRule
import com.example.kaiju_grupo3.model.Movimiento
import com.example.kaiju_grupo3.model.Producto
import com.example.kaiju_grupo3.model.TipoMovimiento
import com.example.kaiju_grupo3.repository.InventarioRepository
import com.example.kaiju_grupo3.repository.SinConexionException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InventarioViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val app = mockk<Application>(relaxed = true)
    private val repo = mockk<InventarioRepository>()

    private fun producto(
        id: Long, nombre: String, codigo: String, categoria: String, stock: Int, minimo: Int
    ) = Producto(
        id = id, codigo = codigo, nombre = nombre, categoria = categoria,
        tipo = "Producto terminado", precio = 1000, stock = stock, stockMinimo = minimo
    )

    private val polera = producto(1, "Polera", "KJ-0001", "Ropa", stock = 10, minimo = 2)
    private val jockey = producto(2, "Jockey", "KJ-0002", "Accesorios", stock = 1, minimo = 3)   // stock bajo
    private val cereal = producto(3, "Barra de cereal", "KJ-0003", "Alimentos", stock = 50, minimo = 5)

    private fun crearVm(
        productos: List<Producto> = listOf(polera, jockey, cereal),
        movimientos: List<Movimiento> = emptyList(),
        sync: Result<Unit> = Result.success(Unit)
    ): InventarioViewModel {
        every { repo.productos } returns flowOf(productos)
        every { repo.movimientos } returns flowOf(movimientos)
        coEvery { repo.sincronizar() } returns sync
        return InventarioViewModel(app, repo)
    }

    // Los StateFlow con WhileSubscribed solo trabajan si alguien los está mirando.
    private fun TestScope.observar(vararg flujos: Flow<*>) {
        flujos.forEach { f ->
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { f.collect() }
        }
    }

    // ---------- Sincronización ----------

    @Test
    fun `al crearse sincroniza con el servidor`() = runTest {
        crearVm()

        coVerify(exactly = 1) { repo.sincronizar() }
    }

    @Test
    fun `sincronizacion correcta deja errorSync en null y termina de cargar`() = runTest {
        val vm = crearVm()

        assertNull(vm.errorSync.value)
        assertFalse(vm.cargando.value)
    }

    @Test
    fun `sincronizacion fallida expone el mensaje de error`() = runTest {
        val vm = crearVm(sync = Result.failure(SinConexionException()))

        assertEquals("No se pudo conectar con el servidor", vm.errorSync.value)
        assertFalse(vm.cargando.value)
    }

    @Test
    fun `reintentar sincronizar limpia el error cuando ahora funciona`() = runTest {
        val vm = crearVm(sync = Result.failure(SinConexionException()))
        coEvery { repo.sincronizar() } returns Result.success(Unit)

        vm.sincronizar()

        assertNull(vm.errorSync.value)
    }

    // ---------- Filtros ----------

    @Test
    fun `sin filtros la lista muestra todos los productos`() = runTest {
        val vm = crearVm()
        observar(vm.productos)

        assertEquals(3, vm.productos.value.size)
    }

    @Test
    fun `buscar por nombre filtra la lista sin importar mayusculas`() = runTest {
        val vm = crearVm()
        observar(vm.productos)

        vm.onBusquedaChange("POLE")

        assertEquals(listOf(polera), vm.productos.value)
    }

    @Test
    fun `buscar por codigo y por categoria tambien funciona`() = runTest {
        val vm = crearVm()
        observar(vm.productos)

        vm.onBusquedaChange("kj-0002")
        assertEquals(listOf(jockey), vm.productos.value)

        vm.onBusquedaChange("alimentos")
        assertEquals(listOf(cereal), vm.productos.value)
    }

    @Test
    fun `una busqueda sin coincidencias deja la lista vacia`() = runTest {
        val vm = crearVm()
        observar(vm.productos)

        vm.onBusquedaChange("zzz")

        assertTrue(vm.productos.value.isEmpty())
    }

    @Test
    fun `solo stock bajo muestra unicamente los productos bajo el minimo`() = runTest {
        val vm = crearVm()
        observar(vm.productos)

        vm.onSoloStockBajoChange(true)

        assertEquals(listOf(jockey), vm.productos.value)
        assertTrue(vm.soloStockBajo.value)
    }

    @Test
    fun `la busqueda y el filtro de stock bajo se combinan`() = runTest {
        val vm = crearVm()
        observar(vm.productos)

        vm.onSoloStockBajoChange(true)
        vm.onBusquedaChange("polera")   // la polera no tiene stock bajo

        assertTrue(vm.productos.value.isEmpty())
    }

    // ---------- Resumen y movimientos ----------

    @Test
    fun `el resumen cuenta productos unidades y stock bajo`() = runTest {
        val vm = crearVm()
        observar(vm.resumen)

        val r = vm.resumen.value
        assertEquals(3, r.totalProductos)
        assertEquals(61, r.totalUnidades)
        assertEquals(1, r.productosStockBajo)
    }

    @Test
    fun `los movimientos incluyen el nombre del producto o Producto eliminado`() = runTest {
        val movs = listOf(
            Movimiento(id = 1, productoId = 1, tipo = TipoMovimiento.ENTRADA, cantidad = 5, stockResultante = 15, fecha = 0),
            Movimiento(id = 2, productoId = 99, tipo = TipoMovimiento.SALIDA, cantidad = 1, stockResultante = 0, fecha = 0)
        )
        val vm = crearVm(movimientos = movs)
        observar(vm.movimientos)

        val lista = vm.movimientos.value
        assertEquals("Polera", lista[0].nombreProducto)
        assertEquals("Producto eliminado", lista[1].nombreProducto)
    }

    @Test
    fun `producto y movimientosDe leen del repositorio`() = runTest {
        val vm = crearVm()
        every { repo.observarProducto(1) } returns flowOf(polera)
        every { repo.observarMovimientosDe(1) } returns flowOf(emptyList())

        assertEquals(polera, vm.producto(1).first())
        assertTrue(vm.movimientosDe(1).first().isEmpty())
    }

    // ---------- Eliminar ----------

    @Test
    fun `eliminarProducto correcto llama onExito`() = runTest {
        val vm = crearVm()
        coEvery { repo.eliminarProducto(polera) } returns Result.success(Unit)
        var exito = false

        vm.eliminarProducto(polera, onExito = { exito = true }, onError = { })

        assertTrue(exito)
        coVerify { repo.eliminarProducto(polera) }
    }

    @Test
    fun `eliminarProducto con error llama onError con el mensaje`() = runTest {
        val vm = crearVm()
        coEvery { repo.eliminarProducto(polera) } returns Result.failure(Exception("No existe"))
        var mensaje: String? = null

        vm.eliminarProducto(polera, onExito = { }, onError = { mensaje = it })

        assertEquals("No existe", mensaje)
    }

    @Test
    fun `eliminarProducto con error sin mensaje usa el texto por defecto`() = runTest {
        val vm = crearVm()
        coEvery { repo.eliminarProducto(polera) } returns Result.failure(Exception())
        var mensaje: String? = null

        vm.eliminarProducto(polera, onExito = { }, onError = { mensaje = it })

        assertEquals("No se pudo eliminar el producto", mensaje)
    }
}