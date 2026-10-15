package com.example.kaiju_grupo3.ui.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val localeChile = Locale("es", "CL")

// 9990 -> "$9.990"
fun formatearPrecio(valor: Int): String =
    NumberFormat.getCurrencyInstance(localeChile).apply { maximumFractionDigits = 0 }.format(valor)

// Milisegundos -> "13/10/2026 20:15"
fun formatearFecha(milisegundos: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", localeChile).format(Date(milisegundos))
