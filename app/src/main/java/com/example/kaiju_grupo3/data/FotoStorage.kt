package com.example.kaiju_grupo3.data

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import androidx.core.net.toUri

object FotoStorage {

    private const val CARPETA = "fotos"

    private fun carpeta(context: Context): File =
        File(context.filesDir, CARPETA).also { it.mkdirs() }

    private fun autoridad(context: Context) = "${context.packageName}.fileprovider"

    private fun nuevoArchivo(context: Context) =
        File(carpeta(context), "prod_${System.currentTimeMillis()}.jpg")
    fun crearUriParaCamara(context: Context): Uri =
        FileProvider.getUriForFile(context, autoridad(context), nuevoArchivo(context))

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
    fun eliminar(context: Context, uriTexto: String?) {
        if (uriTexto == null) return
        val uri = uriTexto.toUri()
        if (uri.authority != autoridad(context)) return
        val nombre = uri.lastPathSegment ?: return
        File(carpeta(context), File(nombre).name).delete()
    }
}
