package com.example.kaiju_grupo3.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kaiju_grupo3.model.InventarioUiEstado
import com.example.kaiju_grupo3.ui.components.FiltrosInventario
import com.example.kaiju_grupo3.ui.components.InventarioAcciones
import com.example.kaiju_grupo3.ui.components.MensajeCarga
import com.example.kaiju_grupo3.ui.components.MensajeSinResultados
import com.example.kaiju_grupo3.ui.components.ProductoCard
import com.example.kaiju_grupo3.ui.components.ResumenInventarioCard

@Composable
fun InventarioMediana(
    estado: InventarioUiEstado,
    acciones: InventarioAcciones,
    innerPadding: PaddingValues
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) { ResumenInventarioCard(estado.resumen) }
        item(span = { GridItemSpan(maxLineSpan) }) {
            FiltrosInventario(estado, acciones, enFila = true)
        }

        when {
            estado.cargando -> item(span = { GridItemSpan(maxLineSpan) }) { MensajeCarga() }
            estado.productos.isEmpty() ->
                item(span = { GridItemSpan(maxLineSpan) }) { MensajeSinResultados() }
            else -> items(items = estado.productos, key = { it.id }) { producto ->
                ProductoCard(
                    producto = producto,
                    onClick = { acciones.onProductoClick(producto) },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}
