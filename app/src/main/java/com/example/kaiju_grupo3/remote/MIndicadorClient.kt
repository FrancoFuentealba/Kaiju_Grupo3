package com.example.kaiju_grupo3.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MindicadorClient {

    val api: MindicadorApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://mindicador.cl/api/")   // debe terminar en "/"
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MindicadorApi::class.java)
    }
}