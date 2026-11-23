package com.example.kaiju_grupo3.repository

import android.util.Log
import com.example.kaiju_grupo3.model.ValorIndicador
import com.example.kaiju_grupo3.remote.MindicadorClient
import com.example.kaiju_grupo3.remote.RespaldoDolarClient
import java.time.Instant

class DolarRepository {
    suspend fun obtenerDolarHoy(): ValorIndicador? =
        try {
            MindicadorClient.api.getDolar().serie.maxByOrNull { it.fecha }
        } catch (e: Exception) {
            Log.w("DolarRepository", "mindicador.cl falló, usando respaldo", e)
            obtenerDeRespaldo()
        }

    private suspend fun obtenerDeRespaldo(): ValorIndicador? {
        val clp = RespaldoDolarClient.api.getUsd().rates?.get("CLP") ?: return null
        return ValorIndicador(Instant.now().toString(), clp)
    }
}