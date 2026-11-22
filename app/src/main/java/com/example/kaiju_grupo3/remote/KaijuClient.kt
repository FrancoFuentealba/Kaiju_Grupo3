package com.example.kaiju_grupo3.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Cliente Retrofit del backend propio de Kaiju. Es independiente de RetrofitInstance (API externa).
object KaijuClient {

    // Emulador de Android Studio: 10.0.2.2 es el computador donde corre el backend.
    // Teléfono físico: la IP del computador en la red WiFi, por ejemplo "http://192.168.1.50:8080/api/v1/".
    // Debe terminar en "/".
    const val BASE_URL = "http://192.168.1.12:8080/api/v1/"

    val api: KaijuApi by lazy {
        val http = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(http)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KaijuApi::class.java)
    }
}
