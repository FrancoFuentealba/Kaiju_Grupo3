package com.example.kaiju_grupo3.remote

import com.example.kaiju_grupo3.remote.dto.MovimientoDto
import com.example.kaiju_grupo3.remote.dto.MovimientoRequestDto
import com.example.kaiju_grupo3.remote.dto.ProductoDto
import com.example.kaiju_grupo3.remote.dto.ProductoRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Endpoints del backend de Kaiju (Spring Boot). Las rutas van SIN "/" inicial:
// con "/" Retrofit descartaría "/api/v1/" de la URL base.
interface KaijuApi {

    @GET("productos")
    suspend fun listarProductos(): List<ProductoDto>

    @GET("productos/{id}")
    suspend fun obtenerProducto(@Path("id") id: Long): ProductoDto

    @POST("productos")
    suspend fun crearProducto(@Body cuerpo: ProductoRequestDto): ProductoDto

    @PUT("productos/{id}")
    suspend fun editarProducto(@Path("id") id: Long, @Body cuerpo: ProductoRequestDto): ProductoDto

    @DELETE("productos/{id}")
    suspend fun eliminarProducto(@Path("id") id: Long)

    @GET("productos/{id}/movimientos")
    suspend fun movimientosDe(@Path("id") id: Long): List<MovimientoDto>

    @POST("productos/{id}/movimientos")
    suspend fun registrarMovimiento(@Path("id") id: Long, @Body cuerpo: MovimientoRequestDto): MovimientoDto

    @GET("movimientos")
    suspend fun listarMovimientos(): List<MovimientoDto>
}
