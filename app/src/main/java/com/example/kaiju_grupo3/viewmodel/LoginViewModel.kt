package com.example.kaiju_grupo3.viewmodel

import androidx.lifecycle.ViewModel
import com.example.kaiju_grupo3.model.LoginEstado
import com.example.kaiju_grupo3.model.UsuarioUiEstado
import com.example.kaiju_grupo3.model.ValidacionesLogin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Formulario de login. Mismo patrón que UsuarioViewModel.
class LoginViewModel : ViewModel() {

    private val _estado = MutableStateFlow(LoginEstado())
    val estado: StateFlow<LoginEstado> = _estado.asStateFlow()

    fun onCorreoChange(valor: String) =
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null, general = null)) }

    fun onClaveChange(valor: String) =
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null, general = null)) }

    // Devuelve true si se puede entrar. "registrado" es el usuario creado en la pantalla de registro.
    fun iniciarSesion(registrado: UsuarioUiEstado?): Boolean {
        val s = _estado.value
        val errores = ValidacionesLogin.validar(s.correo, s.clave)
        if (errores.hayErrores()) {
            _estado.update { it.copy(errores = errores) }
            return false
        }
        if (!ValidacionesLogin.credencialesValidas(s.correo, s.clave, registrado)) {
            _estado.update { it.copy(errores = errores.copy(general = "Correo o contraseña incorrectos")) }
            return false
        }
        return true
    }
}
