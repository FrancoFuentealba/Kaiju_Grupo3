package com.example.kaiju_grupo3.remote

import retrofit2.http.GET

data class RespaldoDolarResponse(
    val result: String?,
    val rates: Map<String, Double>?
)

interface RespaldoDolarApi {
    @GET("latest/USD")
    suspend fun getUsd(): RespaldoDolarResponse
}