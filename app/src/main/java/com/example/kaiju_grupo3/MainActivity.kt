package com.example.kaiju_grupo3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kaiju_grupo3.navigation.NavigationEvent
import com.example.kaiju_grupo3.navigation.Screen
import com.example.kaiju_grupo3.ui.screens.DetalleProductoScreen
import com.example.kaiju_grupo3.ui.screens.LoginScreen
import com.example.kaiju_grupo3.ui.screens.MainInventarioScreen
import com.example.kaiju_grupo3.ui.screens.MovimientoFormScreen
import com.example.kaiju_grupo3.ui.screens.MovimientosScreen
import com.example.kaiju_grupo3.ui.screens.ProductoFormScreen
import com.example.kaiju_grupo3.ui.screens.ProfileScreen
import com.example.kaiju_grupo3.ui.screens.RegistroScreen
import com.example.kaiju_grupo3.ui.screens.ResumenScreen
import com.example.kaiju_grupo3.ui.screens.SettingsScreen
import com.example.kaiju_grupo3.ui.theme.Kaiju_grupo3Theme
import com.example.kaiju_grupo3.viewmodel.InventarioViewModel
import com.example.kaiju_grupo3.viewmodel.MainViewModel
import com.example.kaiju_grupo3.viewmodel.UsuarioViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Kaiju_grupo3Theme {
                // ViewModels a nivel de Activity: se comparten entre pantallas.
                val mainViewModel: MainViewModel = viewModel()
                val usuarioViewModel: UsuarioViewModel = viewModel()          // Registro y Resumen
                val inventarioViewModel: InventarioViewModel = viewModel()    // Inventario, Movimientos y Detalle
                val navController = rememberNavController()

                // Escucha los eventos de navegación que emite el MainViewModel.
                LaunchedEffect(Unit) {
                    mainViewModel.navigationEvents.collectLatest { event ->
                        when (event) {
                            is NavigationEvent.NavigateTo -> {
                                navController.navigate(event.route.route) {
                                    event.popUpToRoute?.let { target ->
                                        popUpTo(target.route) { inclusive = event.inclusive }
                                    }
                                    launchSingleTop = event.singleTop
                                }
                            }
                            is NavigationEvent.PopBackStack -> navController.popBackStack()
                            is NavigationEvent.NavigateUp -> navController.navigateUp()
                        }
                    }
                }

                // Cada pantalla maneja su propio padding (KaijuTabScaffold / safeDrawingPadding)
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Login.route
                    ) {
                        composable(Screen.Login.route) {
                            LoginScreen(navController = navController)
                        }
                        composable(Screen.Registro.route) {
                            RegistroScreen(navController = navController, viewModel = usuarioViewModel)
                        }
                        composable(Screen.Resumen.route) {
                            ResumenScreen(viewModel = usuarioViewModel)
                        }

                        // --- Pestañas principales ---
                        composable(Screen.Inventario.route) {
                            MainInventarioScreen(mainViewModel, inventarioViewModel)
                        }
                        composable(Screen.Movimientos.route) {
                            MovimientosScreen(mainViewModel, inventarioViewModel)
                        }
                        composable(Screen.Profile.route) {
                            ProfileScreen(viewModel = mainViewModel)
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(viewModel = mainViewModel)
                        }

                        // --- Pantalla con argumento: producto/{productoId} ---
                        composable(
                            route = Screen.Detalle.RUTA,
                            arguments = listOf(navArgument(Screen.Detalle.ARG_ID) { type = NavType.LongType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getLong(Screen.Detalle.ARG_ID) ?: -1L
                            DetalleProductoScreen(id, mainViewModel, inventarioViewModel)
                        }

                        // --- Formularios ---
                        composable(Screen.ProductoNuevo.route) {
                            ProductoFormScreen(productoId = null, mainViewModel = mainViewModel)
                        }
                        composable(
                            route = Screen.ProductoEditar.RUTA,
                            arguments = listOf(navArgument(Screen.ProductoEditar.ARG_ID) { type = NavType.LongType })
                        ) { backStackEntry ->
                            val productoId = backStackEntry.arguments?.getLong(Screen.ProductoEditar.ARG_ID) ?: -1L
                            ProductoFormScreen(productoId = productoId, mainViewModel = mainViewModel)
                        }
                        composable(
                            route = Screen.MovimientoNuevo.RUTA,
                            arguments = listOf(navArgument(Screen.MovimientoNuevo.ARG_ID) { type = NavType.LongType })
                        ) { backStackEntry ->
                            val productoId = backStackEntry.arguments?.getLong(Screen.MovimientoNuevo.ARG_ID) ?: -1L
                            MovimientoFormScreen(productoId = productoId, mainViewModel = mainViewModel)
                        }
                    }
                }
            }
        }
    }
}