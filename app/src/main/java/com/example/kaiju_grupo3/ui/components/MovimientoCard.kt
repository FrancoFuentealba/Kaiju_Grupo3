package com.example.kaiju_grupo3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kaiju_grupo3.model.Movimiento
import com.example.kaiju_grupo3.model.TipoMovimiento
import com.example.kaiju_grupo3.ui.utils.formatearFecha

@Composable
fun MovimientoCard(
    movimiento: Movimiento,
    nombreProducto: String?,
    modifier: Modifier = Modifier
) {
    val colores = MaterialTheme.colorScheme
    val (icono, color, cantidadTexto) = when (movimiento.tipo) {
        TipoMovimiento.ENTRADA -> Triple(Icons.Filled.ArrowDownward, colores.tertiary, "+${movimiento.cantidad}")
        TipoMovimiento.SALIDA -> Triple(Icons.Filled.ArrowUpward, colores.error, "-${movimiento.cantidad}")
        TipoMovimiento.VENTA -> Triple(Icons.Filled.ShoppingCart, colores.primary, "-${movimiento.cantidad}")
        TipoMovimiento.AJUSTE -> Triple(Icons.Filled.Tune, colores.secondary, "= ${movimiento.cantidad}")
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colores.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icono, contentDescription = movimiento.tipo.etiqueta, tint = color)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nombreProducto ?: movimiento.tipo.etiqueta,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = if (nombreProducto != null) {
                        "${movimiento.tipo.etiqueta} · ${formatearFecha(movimiento.fecha)}"
                    } else {
                        formatearFecha(movimiento.fecha)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = colores.onSurfaceVariant
                )
                if (movimiento.nota.isNotBlank()) {
                    Text(
                        text = movimiento.nota,
                        style = MaterialTheme.typography.bodySmall,
                        color = colores.onSurfaceVariant
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = cantidadTexto,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = "Stock: ${movimiento.stockResultante}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colores.onSurfaceVariant
                )
            }
        }
    }
}
