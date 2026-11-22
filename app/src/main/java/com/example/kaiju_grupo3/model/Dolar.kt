package com.example.kaiju_grupo3.model

import com.google.gson.annotations.SerializedName

// Respuesta de https://mindicador.cl/api/dolar
data class IndicadorResponse(
    val codigo: String,
    val nombre: String,
    @SerializedName("unidad_medida") val unidadMedida: String,
    val serie: List<ValorIndicador>
)

data class ValorIndicador(
    val fecha: String,
    val valor: Double    // pesos chilenos por 1 dólar
)