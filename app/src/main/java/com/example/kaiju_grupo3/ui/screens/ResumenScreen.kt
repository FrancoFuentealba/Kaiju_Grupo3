package com.example.kaiju_grupo3.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kaiju_grupo3.viewmodel.UsuarioViewModel

@Composable
fun ResumenScreen(viewModel: UsuarioViewModel) {
    // Lee el MISMO ViewModel que usa el registro, por eso ve los mismos datos
    val estado by viewModel.estado.collectAsState()

    Column(Modifier.safeDrawingPadding().padding(16.dp)) {
        Text("Resumen del Registro", style = MaterialTheme.typography.headlineMedium)
        Text("Nombre: ${estado.nombre}")
        Text("Correo: ${estado.correo}")
        Text("Dirección: ${estado.direccion}")
        Text("Contraseña: ${"*".repeat(estado.clave.length)}")
        Text("Términos: ${if (estado.aceptaTerminos) "Aceptados" else "No aceptados"}")
    }
}