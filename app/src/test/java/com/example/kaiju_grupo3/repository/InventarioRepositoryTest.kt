package com.example.kaiju_grupo3.repository

import com.example.kaiju_grupo3.data.MovimientoDao
import com.example.kaiju_grupo3.data.ProductoDao
import com.example.kaiju_grupo3.model.Producto
import com.example.kaiju_grupo3.model.TipoMovimiento
import com.example.kaiju_grupo3.remote.KaijuApi
import com.example.kaiju_grupo3.remote.dto.MovimientoDto
import com.example.kaiju_grupo3.remote.dto.ProductoDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class InventarioRepositoryTest {

    private val productoDao = mockk<ProductoDao>(relaxed = true)
    private val movimientoDao = mockk<MovimientoDao>(relaxed = true)
    private val api = mockk<KaijuApi>()
    private lateinit var repo: InventarioRepository

    private fun dto(id: Long = 1, stock: Int = 5) = ProductoDto(
        id, "P-001", "Polera", null, "Ropa", "ROPA", 9990, stock, 2, "M", "Negro", null, null, null
    )

    private fun producto(id: Long = 0) = Producto(
        id = id, codigo = "P-001", nombre = "Polera", descripcion = "", categoria = "Ropa",
        tipo = "ROPA", precio = 9990, stock = 0, stockMinimo = 2
    )

    private fun httpError(code: Int, json: String) =
        HttpException(Response.error<Any>(code, json.toResponseBody("application/json".toMediaType())))

    @Before
    fun setUp() {
        repo = InventarioRepository(productoDao, movimientoDao, api)
    }

    @Test
    fun `sincronizar guarda en Room lo que responde el servidor`() = runTest {
        coEvery { api.listarProductos() } returns listOf(dto(1), dto(2))
        coEvery { api.listarMovimientos() } returns emptyList()
        coEvery { productoDao.obtenerTodos() } returns emptyList()

        val r = repo.sincronizar()

        assertTrue(r.isSuccess)
        coVerify { productoDao.upsertTodos(match { it.size == 2 }) }
        coVerify { productoDao.eliminarQueNoEstan(listOf(1L, 2L)) }
    }

    @Test
    fun `sincronizar sin conexion devuelve SinConexionException`() = runTest {
        coEvery { api.listarProductos() } throws IOException("sin red")

        val r = repo.sincronizar()

        assertTrue(r.exceptionOrNull() is SinConexionException)
    }

    @Test
    fun `crearProducto con codigo repetido devuelve CodigoDuplicadoException`() = runTest {
        coEvery { api.crearProducto(any()) } throws httpError(409, """{"mensaje":"duplicado"}""")

        val r = repo.crearProducto(producto(), 0)

        assertTrue(r.exceptionOrNull() is CodigoDuplicadoException)
    }

    @Test
    fun `crearProducto correcto devuelve el id del servidor y lo guarda en Room`() = runTest {
        coEvery { api.crearProducto(any()) } returns dto(7, 0)

        val r = repo.crearProducto(producto(), 0)

        assertEquals(7L, r.getOrNull())
        coVerify { productoDao.upsert(match { it.id == 7L }) }
    }

    @Test
    fun `registrarMovimiento con stock insuficiente entrega el mensaje del servidor`() = runTest {
        coEvery { api.registrarMovimiento(any(), any()) } throws
            httpError(422, """{"mensaje":"Stock insuficiente: hay 3 unidades disponibles"}""")

        val r = repo.registrarMovimiento(1, TipoMovimiento.SALIDA, 10, "")

        assertEquals("Stock insuficiente: hay 3 unidades disponibles", r.exceptionOrNull()?.message)
        coVerify(exactly = 0) { productoDao.actualizarStock(any(), any()) }
    }

    @Test
    fun `registrarMovimiento correcto actualiza el stock local`() = runTest {
        coEvery { api.registrarMovimiento(any(), any()) } returns
            MovimientoDto(10, 1, "SALIDA", 2, 3, 1700000000000, "")

        val r = repo.registrarMovimiento(1, TipoMovimiento.SALIDA, 2, "")

        assertTrue(r.isSuccess)
        coVerify { productoDao.actualizarStock(1, 3) }
        coVerify { movimientoDao.upsert(match { it.id == 10L }) }
    }

    @Test
    fun `eliminarProducto borra en el servidor y luego en Room`() = runTest {
        coEvery { api.eliminarProducto(4) } returns Unit

        val r = repo.eliminarProducto(producto(4))

        assertTrue(r.isSuccess)
        coVerify { productoDao.eliminar(match { it.id == 4L }) }
    }

    @Test
    fun `eliminarProducto con error del servidor no toca Room`() = runTest {
        coEvery { api.eliminarProducto(4) } throws httpError(404, """{"mensaje":"No existe"}""")

        val r = repo.eliminarProducto(producto(4))

        assertTrue(r.isFailure)
        coVerify(exactly = 0) { productoDao.eliminar(any()) }
    }
}
