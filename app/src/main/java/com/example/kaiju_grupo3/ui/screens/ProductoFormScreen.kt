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
import com.example.kaiju_grupo3.model.TIPOS_PRODUCTO
import com.example.kaiju_grupo3.ui.components.CampoFormulario
import com.example.kaiju_grupo3.ui.components.SelectorFoto
import com.example.kaiju_grupo3.viewmodel.MainViewModel
import com.example.kaiju_grupo3.viewmodel.ProductoFormViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoFormScreen(
    productoId: Long?,
    mainViewModel: MainViewModel,
    viewModel: ProductoFormViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Al editar, se carga el producto una sola vez.
    LaunchedEffect(productoId) {
        if (productoId != null) viewModel.cargarProducto(productoId)
    }

    val errores = estado.errores

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (estado.esNuevo) "Nuevo producto" else "Editar producto") },
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
        if (estado.cargando) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .imePadding()                       // el teclado no tapa los campos
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SelectorFoto(
                    fotoUri = estado.fotoUri,
                    onFotoDesdeCamara = viewModel::onFotoDesdeCamara,
                    onFotoDesdeGaleria = viewModel::onFotoDesdeGaleria,
                    onQuitarFoto = viewModel::quitarFoto
                )
                Spacer(Modifier.height(12.dp))
                Text("Datos generales", style = MaterialTheme.typography.titleMedium)
                CampoFormulario(
                    valor = estado.codigo,
                    onValorChange = viewModel::onCodigoChange,
                    etiqueta = "Código *",
                    error = errores.codigo,
                    ayuda = "Ej: KJ-0012",
                    capitalizacion = KeyboardCapitalization.Characters
                )
                CampoFormulario(
                    valor = estado.nombre,
                    onValorChange = viewModel::onNombreChange,
                    etiqueta = "Nombre *",
                    error = errores.nombre,
                    capitalizacion = KeyboardCapitalization.Sentences
                )
                CampoFormulario(
                    valor = estado.descripcion,
                    onValorChange = viewModel::onDescripcionChange,
                    etiqueta = "Descripción",
                    error = errores.descripcion,
                    ayuda = "Opcional",
                    capitalizacion = KeyboardCapitalization.Sentences,
                    lineas = 3
                )
                CampoFormulario(
                    valor = estado.categoria,
                    onValorChange = viewModel::onCategoriaChange,
                    etiqueta = "Categoría *",
                    error = errores.categoria,
                    ayuda = "Ej: Ropa, Accesorios, Insumos",
                    capitalizacion = KeyboardCapitalization.Sentences
                )

                Text(
                    text = "Tipo de producto",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TIPOS_PRODUCTO.forEach { tipo ->
                        FilterChip(
                            selected = estado.tipo == tipo,
                            onClick = { viewModel.onTipoChange(tipo) },
                            label = { Text(tipo) }
                        )
                    }
                }
                Text(
                    text = "Precio y stock",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
                CampoFormulario(
                    valor = estado.precio,
                    onValorChange = viewModel::onPrecioChange,
                    etiqueta = "Precio (CLP) *",
                    error = errores.precio,
                    teclado = KeyboardType.Number
                )
                if (estado.esNuevo) {
                    CampoFormulario(
                        valor = estado.stockInicial,
                        onValorChange = viewModel::onStockInicialChange,
                        etiqueta = "Stock inicial *",
                        error = errores.stockInicial,
                        ayuda = "Se registra como un movimiento de entrada",
                        teclado = KeyboardType.Number
                    )
                } else {
                    Text(
                        text = "El stock actual se modifica registrando movimientos desde el detalle del producto.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                CampoFormulario(
                    valor = estado.stockMinimo,
                    onValorChange = viewModel::onStockMinimoChange,
                    etiqueta = "Stock mínimo *",
                    error = errores.stockMinimo,
                    ayuda = "Bajo este nivel se muestra la alerta de stock bajo",
                    teclado = KeyboardType.Number
                )
                Text(
                    text = "Atributos opcionales",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
                CampoFormulario(
                    valor = estado.talla,
                    onValorChange = viewModel::onTallaChange,
                    etiqueta = "Talla",
                    error = errores.talla
                )
                CampoFormulario(
                    valor = estado.color,
                    onValorChange = viewModel::onColorChange,
                    etiqueta = "Color",
                    error = errores.color,
                    capitalizacion = KeyboardCapitalization.Sentences
                )
                CampoFormulario(
                    valor = estado.modelo,
                    onValorChange = viewModel::onModeloChange,
                    etiqueta = "Modelo",
                    error = errores.modelo,
                    capitalizacion = KeyboardCapitalization.Sentences
                )
                CampoFormulario(
                    valor = estado.lote,
                    onValorChange = viewModel::onLoteChange,
                    etiqueta = "Lote",
                    error = errores.lote
                )
                CampoFormulario(
                    valor = estado.vencimiento,
                    onValorChange = viewModel::onVencimientoChange,
                    etiqueta = "Fecha de vencimiento",
                    error = errores.vencimiento,
                    ayuda = "Formato AAAA-MM-DD (ej: 2027-01-20)",
                    accionTeclado = ImeAction.Done
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
                            Toast.makeText(context, "Producto guardado", Toast.LENGTH_SHORT).show()
                            mainViewModel.navigationBack()
                        }
                    },
                    enabled = !estado.guardando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (estado.guardando) "Guardando..." else "Guardar producto")
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