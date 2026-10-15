package com.example.kaiju_grupo3.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kaiju_grupo3.model.Producto
import com.example.kaiju_grupo3.model.stockBajo
import com.example.kaiju_grupo3.ui.components.MovimientoCard
import com.example.kaiju_grupo3.ui.components.StockBajoBadge
import com.example.kaiju_grupo3.ui.utils.formatearPrecio
import com.example.kaiju_grupo3.viewmodel.InventarioViewModel
import com.example.kaiju_grupo3.viewmodel.MainViewModel

// Detalle de un producto: datos, nivel de stock e historial de sus movimientos.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    productoId: Long,
    mainViewModel: MainViewModel,
    inventarioViewModel: InventarioViewModel
) {
    // remember(productoId): el flujo se crea una vez por producto, no en cada recomposición.
    val flujoProducto = remember(productoId) { inventarioViewModel.producto(productoId) }
    val flujoMovimientos = remember(productoId) { inventarioViewModel.movimientosDe(productoId) }
    val producto by flujoProducto.collectAsStateWithLifecycle(initialValue = null)
    val movimientos by flujoMovimientos.collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(producto?.nombre ?: "Detalle") },
                navigationIcon = {
                    IconButton(onClick = { mainViewModel.navigationBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        val actual = producto
        if (actual == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { FotoProductoPlaceholder() }
                item { TarjetaStock(actual) }
                item { TarjetaDatos(actual) }
                item {
                    Text(
                        text = "Historial de movimientos",
                        style = MaterialTheme.typography.titleMedium
                    )
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
    }
}

// Espacio para la foto del producto. En el paso 5 se reemplaza por la imagen tomada con la cámara.
@Composable
private fun FotoProductoPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Inventory2,
            contentDescription = "Producto sin foto",
            modifier = Modifier.size(96.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun TarjetaStock(producto: Producto) {
    val bajo = producto.stockBajo
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${producto.stock} unidades",
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (bajo) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
                if (bajo) StockBajoBadge()
            }
            // La barra llena indica stock >= 2 veces el mínimo.
            LinearProgressIndicator(
                progress = {
                    (producto.stock.toFloat() / (producto.stockMinimo * 2).coerceAtLeast(1)).coerceIn(0f, 1f)
                },
                modifier = Modifier.fillMaxWidth(),
                color = if (bajo) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = "Stock mínimo: ${producto.stockMinimo}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TarjetaDatos(producto: Producto) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilaDato("Código", producto.codigo)
            FilaDato("Categoría", producto.categoria)
            FilaDato("Tipo", producto.tipo)
            FilaDato("Precio", formatearPrecio(producto.precio))
            if (producto.descripcion.isNotBlank()) FilaDato("Descripción", producto.descripcion)
            // Atributos opcionales: solo se muestran los que tiene el producto.
            producto.talla?.let { FilaDato("Talla", it) }
            producto.color?.let { FilaDato("Color", it) }
            producto.modelo?.let { FilaDato("Modelo", it) }
            producto.lote?.let { FilaDato("Lote", it) }
            producto.vencimiento?.let { FilaDato("Vencimiento", it) }
        }
    }
}

@Composable
private fun FilaDato(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.35f)
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.65f)
        )
    }
}
