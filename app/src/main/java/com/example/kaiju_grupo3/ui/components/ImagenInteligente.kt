package com.example.kaiju_grupo3.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.kaiju_grupo3.ui.utils.cargarBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ImagenInteligente(
    fotoUri: String?,
    modifier: Modifier = Modifier,
    forma: Shape = CircleShape,
    maxLadoPx: Int = 800,
    descripcion: String = "Foto del producto"
) {
    val context = LocalContext.current

    // Lee y reduce la imagen en un hilo secundario; se vuelve a leer solo si cambia la Uri.
    val imagen by produceState<ImageBitmap?>(initialValue = null, fotoUri, maxLadoPx) {
        value = if (fotoUri == null) {
            null
        } else {
            withContext(Dispatchers.IO) {
                cargarBitmap(context, Uri.parse(fotoUri), maxLadoPx)?.asImageBitmap()
            }
        }
    }

    Box(
        modifier = modifier
            .clip(forma)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        val bitmap = imagen
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = descripcion,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Inventory2,
                contentDescription = "Producto sin foto",
                modifier = Modifier.fillMaxSize(0.5f),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
