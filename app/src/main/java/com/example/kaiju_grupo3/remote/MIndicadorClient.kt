package com.example.kaiju_grupo3.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
private val httpClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android 14) KaijuApp/1.0")
                    .header("Accept", "application/json")
                    .build()
            )
        }
        .build()
}


object MindicadorClient {

    val api: MindicadorApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://mindicador.cl/api/")// debe terminar en "/"
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MindicadorApi::class.java)
    }
}
object RespaldoDolarClient {
    val api: RespaldoDolarApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://open.er-api.com/v6/")
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RespaldoDolarApi::class.java)
    }
}