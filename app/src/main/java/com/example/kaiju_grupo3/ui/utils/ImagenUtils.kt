package com.example.kaiju_grupo3.ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri

// Lee una imagen desde una Uri y la reduce para que su lado mayor no pase de maxLado píxeles.
// Una foto de cámara pesa varios MB: sin reducirla, la app podría quedarse sin memoria.
// ImageDecoder además respeta la orientación de la foto (no sale girada).
// Devuelve null si la imagen no existe o no se puede leer (nunca lanza excepción).
fun cargarBitmap(context: Context, uri: Uri, maxLado: Int): Bitmap? = try {
    val origen = ImageDecoder.createSource(context.contentResolver, uri)
    ImageDecoder.decodeBitmap(origen) { decoder, info, _ ->
        val ancho = info.size.width
        val alto = info.size.height
        val mayor = maxOf(ancho, alto)
        if (mayor > maxLado) {
            val escala = maxLado.toFloat() / mayor
            decoder.setTargetSize(
                (ancho * escala).toInt().coerceAtLeast(1),
                (alto * escala).toInt().coerceAtLeast(1)
            )
        }
        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
    }
} catch (e: Exception) {
    null
}
