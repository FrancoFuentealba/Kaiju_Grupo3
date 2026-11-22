package com.example.kaiju_grupo3.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kaiju_grupo3.model.ValorIndicador
import java.util.Locale

@Composable
fun DolarCard(
    dolar: ValorIndicador?,
    cargando: Boolean,
    error: String?,
    onReintentar: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Dólar observado", style = MaterialTheme.typography.labelLarge)
                when {
                    cargando -> Text("Cargando...", style = MaterialTheme.typography.bodyMedium)
                    dolar != null -> {
                        val precio = String.format(Locale("es", "CL"), "%,.2f", dolar.valor)
                        Text("$$precio CLP", style = MaterialTheme.typography.titleLarge)
                        Text(dolar.fecha.take(10), style = MaterialTheme.typography.bodySmall)
                    }
                    else -> Text(error ?: "Sin datos", style = MaterialTheme.typography.bodyMedium)
                }
            }
            if (!cargando && dolar == null) {
                TextButton(onClick = onReintentar) { Text("Reintentar") }
            }
        }
    }
}