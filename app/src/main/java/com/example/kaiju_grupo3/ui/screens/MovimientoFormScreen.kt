package com.example.kaiju_grupo3.ui.screens

import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kaiju_grupo3.model.TipoMovimiento
import com.example.kaiju_grupo3.ui.components.CampoFormulario
import com.example.kaiju_grupo3.viewmodel.MainViewModel
import com.example.kaiju_grupo3.viewmodel.MovimientoFormViewModel

// Formulario para registrar una entrada, salida, venta o ajuste de stock de un producto.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovimientoFormScreen(
    productoId: Long,
    mainViewModel: MainViewModel,
    viewModel: MovimientoFormViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(productoId) { viewModel.cargarProducto(productoId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar movimiento") },
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
        val producto = estado.producto
        if (producto == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val errores = estado.errores
            val stockResultante = estado.stockResultante()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Producto al que se le registra el movimiento
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "${producto.codigo} · Stock actual: ${producto.stock} unidades",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "Tipo de movimiento",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TipoMovimiento.entries.forEach { tipo ->
                        FilterChip(
                            selected = estado.tipo == tipo,
                            onClick = { viewModel.onTipoChange(tipo) },
                            label = { Text(tipo.etiqueta) }
                        )
                    }
                }

                val esAjuste = estado.tipo == TipoMovimiento.AJUSTE
                CampoFormulario(
                    valor = estado.cantidad,
                    onValorChange = viewModel::onCantidadChange,
                    etiqueta = if (esAjuste) "Stock contado *" else "Cantidad *",
                    error = errores.cantidad,
                    ayuda = if (esAjuste) "El stock quedará igual al conteo físico" else null,
                    teclado = KeyboardType.Number
                )

                // Vista previa: cómo quedaría el stock si se guarda ahora.
                if (stockResultante != null) {
                    Text(
                        text = "Stock actual ${producto.stock} → quedará en $stockResultante",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                CampoFormulario(
                    valor = estado.nota,
                    onValorChange = viewModel::onNotaChange,
                    etiqueta = "Nota",
                    error = errores.nota,
                    ayuda = "Opcional (ej: compra a proveedor, merma)",
                    capitalizacion = KeyboardCapitalization.Sentences,
                    accionTeclado = ImeAction.Done,
                    lineas = 2
                )

                errores.general?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.guardar {
                            Toast.makeText(context, "Movimiento registrado", Toast.LENGTH_SHORT).show()
                            mainViewModel.navigationBack()
                        }
                    },
                    enabled = !estado.guardando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (estado.guardando) "Guardando..." else "Registrar movimiento")
                }
                OutlinedButton(
                    onClick = { mainViewModel.navigationBack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}