package com.example.kaiju_grupo3.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kaiju_grupo3.viewmodel.EstadoViewModel
import com.example.kaiju_grupo3.viewmodel.MainViewModel

// Pantalla de configuración. Contiene el "Modo especial": un ajuste que se guarda en DataStore
// persiste al cerrar la app y que muestra animaciones.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    estadoViewModel: EstadoViewModel = viewModel()
) {
    val estado by estadoViewModel.activo.collectAsStateWithLifecycle()
    val mostrarMensaje by estadoViewModel.mostrarMensaje.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigationBack() }) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val estadoActual = estado
            if (estadoActual == null) {
                // Mientras se lee el valor guardado en DataStore
                CircularProgressIndicator()
            } else {
                val colorAnimado by animateColorAsState(
                    targetValue = if (estadoActual) Color(0xFF4CAF50) else Color(0xFFB0BEC5),
                    animationSpec = tween(durationMillis = 500),
                    label = "ColorBotonAnimation"
                )

                val textoBoton by remember(estadoActual) {
                    derivedStateOf { if (estadoActual) "Desactivar Modo Especial" else "Activar Modo Especial" }
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

                AnimatedVisibility(visible = mostrarMensaje) {
                    Text(
                        text = "¡Estado guardado exitosamente!",
                        color = Color(0xFF4CAF50),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
