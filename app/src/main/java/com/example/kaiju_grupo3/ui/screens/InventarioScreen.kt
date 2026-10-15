package com.example.kaiju_grupo3.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kaiju_grupo3.model.ResumenInventario
import com.example.kaiju_grupo3.navigation.Screen
import com.example.kaiju_grupo3.ui.components.KaijuTabScaffold
import com.example.kaiju_grupo3.ui.components.ProductoCard
import com.example.kaiju_grupo3.ui.utils.obtenerWindowSizeClass
import com.example.kaiju_grupo3.viewmodel.InventarioViewModel
import com.example.kaiju_grupo3.viewmodel.MainViewModel

// Pestaña principal: resumen, búsqueda, filtro de stock bajo y lista de productos.
@Composable
fun InventarioScreen(
    mainViewModel: MainViewModel,
    inventarioViewModel: InventarioViewModel
) {
    val productos by inventarioViewModel.productos.collectAsStateWithLifecycle()
    val busqueda by inventarioViewModel.busqueda.collectAsStateWithLifecycle()
    val soloStockBajo by inventarioViewModel.soloStockBajo.collectAsStateWithLifecycle()
    val resumen by inventarioViewModel.resumen.collectAsStateWithLifecycle()
    val cargando by inventarioViewModel.cargando.collectAsStateWithLifecycle()

    // Diseño adaptable (guía 9): 1 columna en teléfono, 2 en tablet pequeña, 3 en pantalla grande.
    val columnas = when (obtenerWindowSizeClass().widthSizeClass) {
        WindowWidthSizeClass.Compact -> 1
        WindowWidthSizeClass.Medium -> 2
        else -> 3
    }

    KaijuTabScaffold(
        titulo = "Inventario",
        tabActual = Screen.Inventario,
        viewModel = mainViewModel
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnas),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Los ítems con span = maxLineSpan ocupan todo el ancho de la grilla.
            item(span = { GridItemSpan(maxLineSpan) }) {
                ResumenInventarioCard(resumen)
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = busqueda,
                        onValueChange = inventarioViewModel::onBusquedaChange,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Buscar por nombre, código o categoría") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        trailingIcon = {
                            if (busqueda.isNotEmpty()) {
                                IconButton(onClick = { inventarioViewModel.onBusquedaChange("") }) {
                                    Icon(Icons.Filled.Clear, contentDescription = "Borrar búsqueda")
                                }
                            }
                        }
                    )
                    FilterChip(
                        selected = soloStockBajo,
                        onClick = { inventarioViewModel.onSoloStockBajoChange(!soloStockBajo) },
                        label = { Text("Solo stock bajo") }
                    )
                }
            }

            if (cargando) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (productos.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "No hay productos que coincidan con la búsqueda.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(32.dp)
                    )
                }
            } else {
                items(items = productos, key = { it.id }) { producto ->
                    ProductoCard(
                        producto = producto,
                        onClick = { mainViewModel.navigateTo(Screen.Detalle(producto.id)) }
                    )
                }
            }
        }
    }
}

// Tarjeta con los 3 números clave del inventario.
@Composable
private fun ResumenInventarioCard(resumen: ResumenInventario) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DatoResumen(valor = resumen.totalProductos.toString(), etiqueta = "Productos")
            DatoResumen(valor = resumen.totalUnidades.toString(), etiqueta = "Unidades")
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












