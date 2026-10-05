package com.example.kaiju_grupo3.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kaiju_grupo3.navigation.Screen
import com.example.kaiju_grupo3.viewmodel.MainViewModel

@Composable
fun SettingsScreen(navController: NavController, viewModel: MainViewModel){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Pantalla de Configuracion")
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { viewModel.navigateTo(Screen.Home) }) {
            Text(text = "Volver al Inicio")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.navigateTo(Screen.Profile) }) {
            Text(text = "Ir a Perfil")
        }
    }
}