package com.example.kaiju_grupo3.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kaiju_grupo3.model.InventarioUiEstado
import com.example.kaiju_grupo3.navigation.Screen
import com.example.kaiju_grupo3.ui.components.InventarioAcciones
import com.example.kaiju_grupo3.ui.components.KaijuTabScaffold
import com.example.kaiju_grupo3.viewmodel.InventarioViewModel
import com.example.kaiju_grupo3.viewmodel.MainViewModel
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import com.example.kaiju_grupo3.ui.utils.obtenerWindowSizeClass

@Composable
fun MainInventarioScreen(
    mainViewModel: MainViewModel,
    inventarioViewModel: InventarioViewModel
) {
    val productos by inventarioViewModel.productos.collectAsStateWithLifecycle()
    val busqueda by inventarioViewModel.busqueda.collectAsStateWithLifecycle()
    val soloStockBajo by inventarioViewModel.soloStockBajo.collectAsStateWithLifecycle()
    val resumen by inventarioViewModel.resumen.collectAsStateWithLifecycle()
    val cargando by inventarioViewModel.cargando.collectAsStateWithLifecycle()

    val estado = InventarioUiEstado(productos, busqueda, soloStockBajo, resumen, cargando)

    val acciones = remember(mainViewModel, inventarioViewModel) {
        InventarioAcciones(
            onBusquedaChange = inventarioViewModel::onBusquedaChange,
            onSoloStockBajoChange = inventarioViewModel::onSoloStockBajoChange,
            onProductoClick = { mainViewModel.navigateTo(Screen.Detalle(it.id)) }
        )
    }
    val anchoPantalla = obtenerWindowSizeClass().widthSizeClass
    KaijuTabScaffold(
        titulo = "Inventario",
        tabActual = Screen.Inventario,
        viewModel = mainViewModel
    ) { innerPadding ->
        when (anchoPantalla) {
            WindowWidthSizeClass.Compact -> InventarioCompacta(estado, acciones, innerPadding)
            WindowWidthSizeClass.Medium -> InventarioMediana(estado, acciones, innerPadding)
            else -> InventarioExpandida(estado, acciones, inventarioViewModel, innerPadding)
        }

    }}