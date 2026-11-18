package com.example.kaiju_grupo3.model

// Estado del formulario de inicio de sesión.
data class LoginEstado(
    val correo: String = "",
    val clave: String = "",
    val errores: LoginErrores = LoginErrores()
)

// Un mensaje por campo; "general" es para errores que no son de un campo.
data class LoginErrores(
    val correo: String? = null,
    val clave: String? = null,
    val general: String? = null
) {
    fun hayErrores(): Boolean = correo != null || clave != null || general != null
}
