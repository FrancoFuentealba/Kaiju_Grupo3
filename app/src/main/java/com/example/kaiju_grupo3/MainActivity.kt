package com.example.kaiju_grupo3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kaiju_grupo3.navigation.NavigationEvent
import com.example.kaiju_grupo3.navigation.Screen
import com.example.kaiju_grupo3.ui.screens.HomeScreen
import com.example.kaiju_grupo3.ui.screens.MainInvetarioScreen
import com.example.kaiju_grupo3.ui.screens.ProfileScreen
import com.example.kaiju_grupo3.ui.screens.RegistroScreen   // NUEVO
import com.example.kaiju_grupo3.ui.screens.ResumenScreen    // NUEVO
import com.example.kaiju_grupo3.ui.screens.SettingsScreen
import com.example.kaiju_grupo3.ui.theme.Kaiju_grupo3Theme
import com.example.kaiju_grupo3.viewmodel.MainViewModel
import com.example.kaiju_grupo3.viewmodel.UsuarioViewModel  // NUEVO
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.kaiju_grupo3.ui.screens.LoginScreen

class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Kaiju_grupo3Theme {
                val viewModel: MainViewModel = viewModel()
                val usuarioViewModel: UsuarioViewModel = viewModel()  // se comparte entre Registro y Resumen
                val navController = rememberNavController()

                LaunchedEffect(key1 = Unit) {
                    viewModel.navigationEvents.collectLatest { event ->
                        when (event) {
                            is NavigationEvent.NavigateTo -> {
                                navController.navigate(event.route.route) {
                                    event.popUpToRoute?.let { target ->
                                        popUpTo(target.route) {
                                            inclusive = event.inclusive
                                        }
                                    }
                                    launchSingleTop = event.singleTop
                                }
                            }
                            is NavigationEvent.PopBackStack -> navController.popBackStack()
                            is NavigationEvent.NavigateUp -> navController.navigateUp()
                        }
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(route = Screen.Home.route) {
                            HomeScreen(navController = navController, viewModel = viewModel)
                        }
                        composable(route = Screen.Login.route) {
                            LoginScreen(navController = navController)
                        }
                        composable(route = Screen.Profile.route) {
                            ProfileScreen(navController = navController, viewModel = viewModel)
                        }
                        composable(route = Screen.Settings.route) {
                            SettingsScreen(navController = navController, viewModel = viewModel)
                        }

                        composable(route = Screen.Registro.route) {
                            RegistroScreen(navController = navController, viewModel = usuarioViewModel)
                        }
                        composable(route = Screen.Resumen.route) {
                            ResumenScreen(viewModel = usuarioViewModel)

                        }
                    }
                }
            }
        }
    }
}