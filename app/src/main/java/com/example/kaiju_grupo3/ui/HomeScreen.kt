package com.example.kaiju_grupo3.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kaiju_grupo3.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    //Estructura Scaffold
    Scaffold(
        topBar = {
            TopAppBar(title = {Text("App Kaiju")})
        }
    ) { innerPadding ->
        //Contenedor principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            //espaciado entre elementos
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) { //Elemento de texto con colores
            Text(
            text = "¡Bienvenido!",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge
        )
        //Otro elemento visual
        Text(
            text = "Gestion de inventario activa.",
            color = MaterialTheme.colorScheme.secondary
        )
        Button(onClick = {/*accion futura*/}) {
            Text("Presionar")
        }
            //imagen cargada
        Image(
            painter = painterResource(id = R.drawable.logo_inventa),
            contentDescription = "Logo App",
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            contentScale = ContentScale.Fit
        )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}








































