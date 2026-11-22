package com.example.kaiju_grupo3.repository

import com.example.kaiju_grupo3.model.ValorIndicador
import com.example.kaiju_grupo3.remote.MindicadorClient

class DolarRepository {
    suspend fun obtenerDolarHoy(): ValorIndicador? =
        MindicadorClient.api.getDolar().serie.maxByOrNull { it.fecha }
}