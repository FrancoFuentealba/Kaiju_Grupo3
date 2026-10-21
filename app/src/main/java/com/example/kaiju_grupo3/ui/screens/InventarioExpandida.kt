package com.example.kaiju_grupo3.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kaiju_grupo3.model.InventarioUiEstado
import com.example.kaiju_grupo3.ui.components.FiltrosInventario
import com.example.kaiju_grupo3.ui.components.InventarioAcciones
import com.example.kaiju_grupo3.ui.components.MensajeCarga
import com.example.kaiju_grupo3.ui.components.MensajeSinResultados
import com.example.kaiju_grupo3.ui.components.MovimientoCard
import com.example.kaiju_grupo3.ui.components.ProductoCard
import com.example.kaiju_grupo3.ui.components.ResumenInventarioCard
import com.example.kaiju_grupo3.viewmodel.InventarioViewModel

@Composable
fun InventarioExpandida(
    estado: InventarioUiEstado,
    acciones: InventarioAcciones,
    inventarioViewModel: InventarioViewModel,
    innerPadding: PaddingValues
) {
    var seleccionadoId by rememberSaveable { mutableStateOf<Long?>(null) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        // ----- Panel izquierdo -----
        LazyColumn(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { ResumenInventarioCard(estado.resumen) }
            item { FiltrosInventario(estado, acciones) }

            when {
                estado.cargando -> item { MensajeCarga() }
                estado.productos.isEmpty() -> item { MensajeSinResultados() }
                else -> items(items = estado.productos, key = { it.id }) { producto ->
                    val seleccionado = producto.id == seleccionadoId
                    ProductoCard(
                        producto = producto,
                        onClick = { seleccionadoId = producto.id },
                        modifier = Modifier
                            .animateItem()
                            .then(
                                if (seleccionado) {
                                    Modifier.border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                } else Modifier
                            )
                    )
                }
            }
        }

        VerticalDivider()

        // ----- Panel derecho -----
        Box(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
        ) {
            val id = seleccionadoId
            if (id == null) {
                Text(
                    text = "Selecciona un producto para ver su detalle",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                PanelDetalleProducto(id, inventarioViewModel)
            }
        }
    }
}
@Composable
private fun PanelDetalleProducto(productoId: Long, inventarioViewModel: InventarioViewModel) {
    val flujoProducto = remember(productoId) { inventarioViewModel.producto(productoId) }
    val flujoMovimientos = remember(productoId) { inventarioViewModel.movimientosDe(productoId) }
    val producto by flujoProducto.collectAsStateWithLifecycle(initialValue = null)
    val movimientos by flujoMovimientos.collectAsStateWithLifecycle(initialValue = emptyList())

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        producto?.let { actual ->
            item {
                Text(actual.nombre, style = MaterialTheme.typography.headlineSmall)
            }
            item { TarjetaStock(actual) }
            item { TarjetaDatos(actual) }
        }
        item {
            Text("Historial de movimientos", style = MaterialTheme.typography.titleMedium)
        }
        if (movimientos.isEmpty()) {
            item {
                Text(
                    text = "Este producto aún no tiene movimientos.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(items = movimientos, key = { it.id }) { movimiento ->
                MovimientoCard(movimiento = movimiento, nombreProducto = null)
            }
        }
    }
}
