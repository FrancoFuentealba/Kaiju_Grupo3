package com.example.kaiju_grupo3.ui.components
import com.example.kaiju_grupo3.model.Producto
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.kaiju_grupo3.model.InventarioUiEstado
import com.example.kaiju_grupo3.model.ResumenInventario

class InventarioAcciones(
    val onBusquedaChange: (String) -> Unit,
    val onSoloStockBajoChange: (Boolean) -> Unit,
    val onProductoClick: (Producto) -> Unit
)
@Composable
fun FiltrosInventario(
    estado: InventarioUiEstado,
    acciones: InventarioAcciones,
    modifier: Modifier = Modifier,
    enFila: Boolean = false
) {
    if (enFila) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BuscadorInventario(estado, acciones, Modifier.weight(1f))
            ChipStockBajo(estado, acciones)
        }
    } else {
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            BuscadorInventario(estado, acciones, Modifier.fillMaxWidth())
            ChipStockBajo(estado, acciones)
        }
    }
}

@Composable
private fun BuscadorInventario(
    estado: InventarioUiEstado,
    acciones: InventarioAcciones,
    modifier: Modifier
) {
    OutlinedTextField(
        value = estado.busqueda,
        onValueChange = acciones.onBusquedaChange,
        modifier = modifier,
        singleLine = true,
        label = { Text("Buscar por nombre, código o categoría") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (estado.busqueda.isNotEmpty()) {
                IconButton(onClick = { acciones.onBusquedaChange("") }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Borrar búsqueda")
                }
            }
        }
    )
}

@Composable
private fun ChipStockBajo(estado: InventarioUiEstado, acciones: InventarioAcciones) {
    FilterChip(
        selected = estado.soloStockBajo,
        onClick = { acciones.onSoloStockBajoChange(!estado.soloStockBajo) },
        label = { Text("Solo stock bajo") }
    )
}

@Composable
fun ResumenInventarioCard(resumen: ResumenInventario, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DatoResumen(resumen.totalProductos.toString(), "Productos")
            DatoResumen(resumen.totalUnidades.toString(), "Unidades")
            DatoResumen(
                valor = resumen.productosStockBajo.toString(),
                etiqueta = "Stock bajo",
                destacar = resumen.productosStockBajo > 0
            )
        }
    }
}

@Composable
private fun DatoResumen(valor: String, etiqueta: String, destacar: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = valor,
            style = MaterialTheme.typography.headlineMedium,
            color = if (destacar) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
@Composable
fun MensajeCarga() {
    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun MensajeSinResultados() {
    Text(
        text = "No hay productos que coincidan con la búsqueda.",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(32.dp)
    )
}