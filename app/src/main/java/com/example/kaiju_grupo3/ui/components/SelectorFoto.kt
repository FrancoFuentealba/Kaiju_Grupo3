package com.example.kaiju_grupo3.ui.components

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.kaiju_grupo3.data.FotoStorage

// Foto del producto: muestra la imagen y dos botones (cámara y galería).
// Usa los lanzadores modernos de la guía 13: TakePicture() y GetContent().
// El resultado se entrega por callbacks al ViewModel; este componente no guarda estado de la foto.
@Composable
fun SelectorFoto(
    fotoUri: String?,
    onFotoDesdeCamara: (Uri) -> Unit,
    onFotoDesdeGaleria: (Uri) -> Unit,
    onQuitarFoto: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Uri donde la cámara está guardando la foto. rememberSaveable la conserva si se gira la pantalla.
    var uriCamara by rememberSaveable { mutableStateOf<Uri?>(null) }

    // --- Cámara: TakePicture devuelve true si el usuario aceptó la foto, false si canceló ---
    val camaraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        val uri = uriCamara
        if (exito && uri != null) {
            onFotoDesdeCamara(uri)
        } else {
            // Canceló: se borra el archivo vacío que se había reservado.
            FotoStorage.eliminar(context, uri?.toString())
        }
        uriCamara = null
    }

    fun abrirCamara() {
        val uri = FotoStorage.crearUriParaCamara(context)
        uriCamara = uri
        try {
            camaraLauncher.launch(uri)
        } catch (e: ActivityNotFoundException) {
            // Pasa, por ejemplo, en un emulador sin app de cámara.
            uriCamara = null
            FotoStorage.eliminar(context, uri.toString())
            Toast.makeText(context, "No hay una app de cámara disponible", Toast.LENGTH_LONG).show()
        }
    }

    // --- Permiso de cámara (declarado en el AndroidManifest, se pide al usarlo) ---
    val permisoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) {
            abrirCamara()
        } else {
            Toast.makeText(context, "Sin el permiso de cámara no se puede tomar la foto", Toast.LENGTH_LONG).show()
        }
    }

    fun tomarFoto() {
        val tienePermiso = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (tienePermiso) abrirCamara() else permisoLauncher.launch(Manifest.permission.CAMERA)
    }

    // --- Galería: GetContent devuelve la Uri elegida, o null si el usuario canceló ---
    val galeriaLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) onFotoDesdeGaleria(uri)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ImagenInteligente(
            fotoUri = fotoUri,
            modifier = Modifier.size(160.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { tomarFoto() },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(" Cámara")
            }
            OutlinedButton(
                onClick = { galeriaLauncher.launch("image/*") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(" Galería")
            }
        }
        if (fotoUri != null) {
            TextButton(onClick = onQuitarFoto) { Text("Quitar foto") }
        }
    }
}
