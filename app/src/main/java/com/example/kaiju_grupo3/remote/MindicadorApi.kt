package com.example.kaiju_grupo3.remote

import com.example.kaiju_grupo3.model.IndicadorResponse
import retrofit2.http.GET

interface MindicadorApi {

    @GET("dolar")
    suspend fun getDolar(): IndicadorResponse
}