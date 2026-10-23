package com.example.kaiju_grupo3.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun InventarioCompacta(
    estado: InventarioUiEstado,
    acciones: InventarioAcciones,
    innerPadding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { ResumenInventarioCard(estado.resumen) }
        item { FiltrosInventario(estado, acciones) }

        when {
            estado.cargando -> item { MensajeCarga() }
            estado.productos.isEmpty() -> item { MensajeSinResultados() }
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
