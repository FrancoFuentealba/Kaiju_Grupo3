package com.example.kaiju_grupo3.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kaiju_grupo3.navigation.Screen
import com.example.kaiju_grupo3.viewmodel.EstadoViewModel
import com.example.kaiju_grupo3.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MainViewModel,
    estadoViewModel: EstadoViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val estado = estadoViewModel.activo.collectAsState()
    val mostrarMensaje = estadoViewModel.mostrarMensaje.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(text = "Menu", modifier = Modifier.padding(16.dp))
                NavigationDrawerItem(
                    label = { Text(text = "Ir a Perfil") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        viewModel.navigateTo(Screen.Profile)
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Iniciar sesión") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        viewModel.navigateTo(Screen.Login)
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Pantalla Home") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                if (estado.value == null) {
                    CircularProgressIndicator()
                } else {
                    val estaActivo = estado.value!!

                    val colorAnimado by animateColorAsState(
                        targetValue = if (estaActivo) Color(0xFF4CAF50) else Color(0xFFB0BEC5),
                        animationSpec = tween(durationMillis = 500),
                        label = "ColorBotonAnimation"
                    )

                    val textoBoton by remember(estaActivo) {
                        derivedStateOf { if (estaActivo) "Desactivar Modo Especial" else "Activar Modo Especial" }
                    }

                    Button(
                        onClick = { estadoViewModel.alternarEstado() },
                        colors = ButtonDefaults.buttonColors(containerColor = colorAnimado),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(50.dp)
                    ) {
                        Text(text = textoBoton)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    AnimatedVisibility(visible = mostrarMensaje.value) {
                        Text(
                            text = "¡Estado guardado exitosamente!",
                            color = Color(0xFF4CAF50),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                // ------------------ Tu código anterior (Sin la llave sobrante) ------------------
                Spacer(modifier = Modifier.height(32.dp))
                Text(text = "Bienvenido a la pagina de Inicio (MVVM)")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.navigateTo(Screen.Settings) }) {
                    Text(text = "Ir a Configuracion")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.navigateTo(Screen.Login) }) {
                    Text(text = "Iniciar sesión")
                }
            }
        }
    }
}