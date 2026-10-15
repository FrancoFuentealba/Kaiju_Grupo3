package com.example.kaiju_grupo3.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kaiju_grupo3.navigation.Screen
import com.example.kaiju_grupo3.ui.components.KaijuTabScaffold
import com.example.kaiju_grupo3.viewmodel.MainViewModel

// Pestaña "Perfil". Los datos son ficticios (el caso prohíbe usar datos personales reales).
@Composable
fun ProfileScreen(viewModel: MainViewModel) {
    KaijuTabScaffold(
        titulo = "Perfil",
        tabActual = Screen.Profile,
        viewModel = viewModel
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Foto de perfil",
                modifier = Modifier.size(96.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))
            Text("Usuario de prueba", style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "Rol: Encargado de inventario (demo)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { viewModel.navigateTo(Screen.Settings) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Configuración")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    viewModel.navigateTo(Screen.Login, popUpTo = Screen.Inventario, inclusive = true)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}
