package com.example.kaiju_grupo3.viewmodel

import android.app.Application
import android.net.Uri
import com.example.kaiju_grupo3.MainDispatcherRule
import com.example.kaiju_grupo3.model.Producto
import com.example.kaiju_grupo3.repository.CodigoDuplicadoException
import com.example.kaiju_grupo3.repository.InventarioRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductoFormViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val app = mockk<Application>(relaxed = true)
    private val repo = mockk<InventarioRepository>()
    private lateinit var vm: ProductoFormViewModel

    @Before
    fun setUp() {
        vm = ProductoFormViewModel(app, repo)
    }

    // Deja el formulario completo y válido (producto nuevo).
    private fun llenarFormularioValido() {
        vm.onCodigoChange("kj-0012")
        vm.onNombreChange("Polera")
        vm.onCategoriaChange("Ropa")
        vm.onPrecioChange("9990")
        vm.onStockInicialChange("5")
        vm.onStockMinimoChange("2")
    }

    // ---------- Campos ----------

    @Test
    fun `el codigo se guarda en mayusculas y con maximo 20 caracteres`() {
        vm.onCodigoChange("kj-0012")
        assertEquals("KJ-0012", vm.estado.value.codigo)

        vm.onCodigoChange("a".repeat(30))
        assertEquals(20, vm.estado.value.codigo.length)
    }

    @Test
    fun `precio y stock solo aceptan digitos`() {
        vm.onPrecioChange("12ab.3")
        vm.onStockInicialChange("x4y")
        vm.onStockMinimoChange("--7")

        assertEquals("123", vm.estado.value.precio)
        assertEquals("4", vm.estado.value.stockInicial)
        assertEquals("7", vm.estado.value.stockMinimo)
    }

    @Test
    fun `los campos numericos tienen un largo maximo`() {
        vm.onPrecioChange("1234567890")
        vm.onStockInicialChange("1234567890")

        assertEquals(8, vm.estado.value.precio.length)
        assertEquals(7, vm.estado.value.stockInicial.length)
    }

    @Test
    fun `los campos de texto guardan el valor`() {
        vm.onTipoChange("Insumo")
        vm.onDescripcionChange("Algodón")
        vm.onTallaChange("M")
        vm.onColorChange("Negro")
        vm.onModeloChange("Clásico")
        vm.onLoteChange("L-1")
        vm.onVencimientoChange("2027-01-20xxxx")

        val s = vm.estado.value
        assertEquals("Insumo", s.tipo)
        assertEquals("Algodón", s.descripcion)
        assertEquals("M", s.talla)
        assertEquals("Negro", s.color)
        assertEquals("Clásico", s.modelo)
        assertEquals("L-1", s.lote)
        assertEquals("2027-01-20", s.vencimiento)   // recorta a 10 caracteres
    }

    @Test
    fun `escribir en un campo borra el error de ese campo`() {
        vm.validarFormulario()
        assertEquals("Campo obligatorio", vm.estado.value.errores.nombre)

        vm.onNombreChange("Pol")

        assertNull(vm.estado.value.errores.nombre)
    }

    @Test
    fun `onFotoDesdeCamara guarda la uri de la foto en el estado`() {
        val uri = mockk<Uri>()
        every { uri.toString() } returns "content://kaiju/foto1"

        vm.onFotoDesdeCamara(uri)
        assertEquals("content://kaiju/foto1", vm.estado.value.fotoUri)
    }

    // ---------- Validación ----------

    @Test
    fun `formulario vacio no es valido y marca los errores`() {
        assertFalse(vm.validarFormulario())

        val e = vm.estado.value.errores
        assertEquals("Campo obligatorio", e.codigo)
        assertEquals("Campo obligatorio", e.nombre)
        assertEquals("Campo obligatorio", e.categoria)
        assertEquals("Campo obligatorio", e.precio)
    }

    @Test
    fun `formulario completo es valido`() {
        llenarFormularioValido()

        assertTrue(vm.validarFormulario())
        assertFalse(vm.estado.value.errores.hayErrores())
    }

    // ---------- Guardar (producto nuevo) ----------

    @Test
    fun `guardar un producto nuevo llama a crearProducto con el stock inicial`() = runTest {
        coEvery { repo.crearProducto(any(), any()) } returns Result.success(7L)
        llenarFormularioValido()
        var exito = false

        vm.guardar { exito = true }

        assertTrue(exito)
        coVerify {
            repo.crearProducto(
                match { it.codigo == "KJ-0012" && it.nombre == "Polera" && it.precio == 9990 && it.stockMinimo == 2 },
                5
            )
        }
    }

    @Test
    fun `guardar con el formulario invalido no llama al repositorio`() = runTest {
        var exito = false

        vm.guardar { exito = true }

        assertFalse(exito)
        coVerify(exactly = 0) { repo.crearProducto(any(), any()) }
        coVerify(exactly = 0) { repo.guardarProducto(any()) }
    }

    @Test
    fun `guardar con codigo repetido muestra el error en el campo codigo`() = runTest {
        coEvery { repo.crearProducto(any(), any()) } returns Result.failure(CodigoDuplicadoException("KJ-0012"))
        llenarFormularioValido()
        var exito = false

        vm.guardar { exito = true }

        assertFalse(exito)
        assertEquals("Ya existe un producto con el código KJ-0012", vm.estado.value.errores.codigo)
        assertFalse(vm.estado.value.guardando)
    }

    @Test
    fun `guardar con otro error muestra un mensaje general`() = runTest {
        coEvery { repo.crearProducto(any(), any()) } returns Result.failure(Exception("boom"))
        llenarFormularioValido()

        vm.guardar { }

        assertEquals("No se pudo guardar el producto. Inténtalo de nuevo.", vm.estado.value.errores.general)
        assertFalse(vm.estado.value.guardando)
    }

    @Test
    fun `los campos opcionales vacios se guardan como null`() = runTest {
        coEvery { repo.crearProducto(any(), any()) } returns Result.success(1L)
        llenarFormularioValido()

        vm.guardar { }

        coVerify {
            repo.crearProducto(match { it.talla == null && it.color == null && it.vencimiento == null }, any())
        }
    }

    // ---------- Editar ----------

    private val existente = Producto(
        id = 3, codigo = "KJ-0003", nombre = "Jockey", descripcion = "Gorra", categoria = "Accesorios",
        tipo = "Producto terminado", precio = 5000, stock = 12, stockMinimo = 4, talla = "U"
    )

    @Test
    fun `cargarProducto rellena el formulario con los datos existentes`() = runTest {
        coEvery { repo.obtenerProducto(3) } returns existente

        vm.cargarProducto(3)

        val s = vm.estado.value
        assertEquals(3L, s.id)
        assertEquals("KJ-0003", s.codigo)
        assertEquals("5000", s.precio)
        assertEquals("4", s.stockMinimo)
        assertEquals("U", s.talla)
        assertEquals("", s.color)        // los null quedan como texto vacío
        assertFalse(s.esNuevo)
    }

    @Test
    fun `cargarProducto de un id inexistente muestra un error`() = runTest {
        coEvery { repo.obtenerProducto(99) } returns null

        vm.cargarProducto(99)

        assertEquals("El producto no existe", vm.estado.value.errores.general)
        assertFalse(vm.estado.value.cargando)
    }

    @Test
    fun `cargarProducto no vuelve a cargar si ya se cargo`() = runTest {
        coEvery { repo.obtenerProducto(3) } returns existente

        vm.cargarProducto(3)
        vm.cargarProducto(3)

        coVerify(exactly = 1) { repo.obtenerProducto(3) }
    }

    @Test
    fun `guardar una edicion llama a guardarProducto y conserva el stock`() = runTest {
        coEvery { repo.obtenerProducto(3) } returns existente
        coEvery { repo.guardarProducto(any()) } returns Result.success(3L)
        vm.cargarProducto(3)
        vm.onNombreChange("Jockey nuevo")
        var exito = false

        vm.guardar { exito = true }

        assertTrue(exito)
        coVerify { repo.guardarProducto(match { it.id == 3L && it.nombre == "Jockey nuevo" && it.stock == 12 }) }
        coVerify(exactly = 0) { repo.crearProducto(any(), any()) }
    }
}