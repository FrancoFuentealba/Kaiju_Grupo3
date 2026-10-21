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
import com.example.kaiju_grupo3.ui.screens.MovimientosScreen
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
                val mainViewModel: MainViewModel = viewModel()
                val usuarioViewModel: UsuarioViewModel = viewModel()
                val inventarioViewModel: InventarioViewModel = viewModel()
                val navController = rememberNavController()

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

                        composable(Screen.Movimientos.route) {
                            MovimientosScreen(mainViewModel, inventarioViewModel)
                        }
                        composable(Screen.Profile.route) {
                            ProfileScreen(viewModel = mainViewModel)
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(viewModel = mainViewModel)
                        }
                        composable(Screen.Inventario.route) {
                            MainInventarioScreen(mainViewModel, inventarioViewModel)
                        }
                        composable(
                            route = Screen.Detalle.RUTA,
                            arguments = listOf(navArgument(Screen.Detalle.ARG_ID) { type = NavType.LongType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getLong(Screen.Detalle.ARG_ID) ?: 0L
                            DetalleProductoScreen(id, mainViewModel, inventarioViewModel)
                        }
                    }
                }
            }
        }
    }
}