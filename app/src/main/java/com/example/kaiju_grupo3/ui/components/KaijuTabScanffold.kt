package com.example.kaiju_grupo3.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.kaiju_grupo3.navigation.Screen
import com.example.kaiju_grupo3.viewmodel.MainViewModel
import kotlinx.coroutines.launch

private data class TabItem(
    val pantalla: Screen,
    val etiqueta: String,
    val icono: ImageVector
)

private val tabs = listOf(
    TabItem(Screen.Inventario, "Inventario", Icons.Filled.Inventory2),
    TabItem(Screen.Movimientos, "Movimientos", Icons.Filled.SwapVert),
    TabItem(Screen.Profile, "Perfil", Icons.Filled.Person)
)

// Estructura común de las 3 pestañas principales:
// barra superior con botón de menú + menú lateral (drawer) + barra de navegación inferior.
// Como solo hay UN Scaffold por pantalla, no se duplican los márgenes del sistema.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KaijuTabScaffold(
    titulo: String,
    tabActual: Screen,
    viewModel: MainViewModel,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Kaiju",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp)
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Configuración") },
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        viewModel.navigateTo(Screen.Settings)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
                    icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        // Vuelve al login y borra el resto de la pila de navegación.
                        viewModel.navigateTo(Screen.Login, popUpTo = Screen.Inventario, inclusive = true)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(titulo) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Abrir menú")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            bottomBar = {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = tab.pantalla == tabActual,
                            onClick = {
                                if (tab.pantalla != tabActual) {
                                    // La pila siempre parte de Inventario: así "atrás" desde otra pestaña
                                    // vuelve al inventario y no se acumulan pantallas repetidas.
                                    viewModel.navigateTo(
                                        screen = tab.pantalla,
                                        popUpTo = Screen.Inventario,
                                        singleTop = true
                                    )
                                }
                            },
                            icon = { Icon(tab.icono, contentDescription = tab.etiqueta) },
                            label = { Text(tab.etiqueta) }
                        )
                    }
                }
            },
            floatingActionButton = floatingActionButton,
            content = content
        )
    }
}

