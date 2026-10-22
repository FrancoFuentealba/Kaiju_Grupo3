package com.example.kaiju_grupo3.data

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

// Almacenamiento de las fotos de productos (guía 13: FileProvider).
// Todas las fotos quedan en la carpeta privada de la app: files/fotos/
// Se comparten con la app de cámara a través de un FileProvider (ver AndroidManifest.xml y res/xml/file_paths.xml).
object FotoStorage {

    private const val CARPETA = "fotos"

    private fun carpeta(context: Context): File =
        File(context.filesDir, CARPETA).also { it.mkdirs() }

    // Debe coincidir con "android:authorities" del <provider> en el AndroidManifest.
    private fun autoridad(context: Context) = "${context.packageName}.fileprovider"

    private fun nuevoArchivo(context: Context) =
        File(carpeta(context), "prod_${System.currentTimeMillis()}.jpg")

    // Uri donde la cámara va a guardar la foto (el archivo se crea cuando el usuario acepta la foto).
    fun crearUriParaCamara(context: Context): Uri =
        FileProvider.getUriForFile(context, autoridad(context), nuevoArchivo(context))

    // Las imágenes de la galería son permisos temporales: se COPIAN a la carpeta de la app
    // para que sigan disponibles después de cerrar la app. Devuelve null si falla.
    suspend fun copiarDesdeGaleria(context: Context, origen: Uri): Uri? = withContext(Dispatchers.IO) {
        try {
            val destino = nuevoArchivo(context)
            val entrada = context.contentResolver.openInputStream(origen) ?: return@withContext null
            entrada.use { input -> destino.outputStream().use { output -> input.copyTo(output) } }
            FileProvider.getUriForFile(context, autoridad(context), destino)
        } catch (e: Exception) {
            null
        }
    }

    // Borra una foto de la carpeta de la app. Ignora cualquier Uri que no sea de este FileProvider.
    fun eliminar(context: Context, uriTexto: String?) {
        if (uriTexto == null) return
        val uri = Uri.parse(uriTexto)
        if (uri.authority != autoridad(context)) return
        val nombre = uri.lastPathSegment ?: return
        File(carpeta(context), File(nombre).name).delete()
    }
}
