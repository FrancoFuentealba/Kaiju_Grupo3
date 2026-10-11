package com.example.kaiju_grupo3.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.kaiju_grupo3.model.UsuarioErrores
import com.example.kaiju_grupo3.model.UsuarioUiEstado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class UsuarioViewModel : ViewModel() {

    // Estado interno: aquí se guarda lo que escribe el usuario
    private val _estado = MutableStateFlow(UsuarioUiEstado())

    // Estado que ven las pantallas (solo lectura)
    val estado: StateFlow<UsuarioUiEstado> = _estado

    // Cada vez que el usuario escribe, se guarda el valor y se borra el error de ese campo
    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    fun onDireccionChange(valor: String) {
        _estado.update { it.copy(direccion = valor, errores = it.errores.copy(direccion = null)) }
    }

    fun onAceptarTerminosChange(valor: Boolean) {
        _estado.update { it.copy(aceptaTerminos = valor, errores = it.errores.copy(terminos = null)) }
    }

    // Revisa todo el formulario. Devuelve true si está bien, false si hay errores
    fun validarFormulario(): Boolean {
        val s = _estado.value
        val errores = UsuarioErrores(
            nombre = if (s.nombre.isBlank()) "Campo obligatorio" else null,
            correo = if (!Patterns.EMAIL_ADDRESS.matcher(s.correo).matches()) "Correo inválido" else null,
            clave = if (s.clave.length < 6) "Debe tener al menos 6 caracteres" else null,
            direccion = if (s.direccion.isBlank()) "Campo obligatorio" else null,
            terminos = if (!s.aceptaTerminos) "Debes aceptar los términos" else null
        )
        val hayErrores = listOfNotNull(
            errores.nombre, errores.correo, errores.clave, errores.direccion, errores.terminos
        ).isNotEmpty()

        _estado.update { it.copy(errores = errores) }
        return !hayErrores
    }
}