package com.example.kaiju_grupo3.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kaiju_grupo3.R

//1. Vista para telefonos

@Preview(name = "Compact", widthDp = 360, heightDp = 800)
@Composable
fun InventarioScreeenCompacta() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_inventa),
            contentDescription = "Logo Kaiju",
            modifier = Modifier.size(150.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Vista Compacta - Telefono",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

//2. Vista para tablets pequeñas o telefonos horizontales


@Preview(name = "Medium", widthDp = 600, heightDp = 480)
@Composable
fun InvetarioScreeenMediana(){
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_inventa),
            contentDescription = "Logo Kaiju",
            modifier = Modifier.size(200.dp)
        )
        Text(
            text = "Vista Mediana - Tablet",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

//3. Vista para tablets grandes o monitores

@Preview(name = "Expanded", widthDp = 840, heightDp = 900)
@Composable
fun InvetarioScreeenExpandida(){
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(64.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_inventa),
            contentDescription = "Logo Kaiju",
            modifier = Modifier.size(250.dp)
        )
        Spacer(modifier = Modifier.width(48.dp))
        Column{
            Text(
                text = "Vista Expandida - Pantalla grande",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Detalle del inevtario completo.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}












