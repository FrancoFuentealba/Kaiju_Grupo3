package com.example.kaiju_grupo3.model

// Reglas del inicio de sesión. Funciones puras para poder probarlas con tests unitarios.
object ValidacionesLogin {

    // Usuario de prueba.
    const val CORREO_PRUEBA = "prueba@kaiju.cl"
    const val CLAVE_PRUEBA = "kaiju123"

    private val REGEX_CORREO = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    // Revisa el formato de los campos (no si las credenciales son correctas).
    fun validar(correo: String, clave: String): LoginErrores = LoginErrores(
        correo = when {
            correo.isBlank() -> "Campo obligatorio"
            !REGEX_CORREO.matches(correo.trim()) -> "Correo inválido"
            else -> null
        },
        clave = when {
            clave.isBlank() -> "Campo obligatorio"
            clave.length < 6 -> "Debe tener al menos 6 caracteres"
            else -> null
        }
    )

    // Las credenciales sirven si son las del usuario de prueba o las del usuario recién registrado.
    fun credencialesValidas(correo: String, clave: String, registrado: UsuarioUiEstado?): Boolean {
        val c = correo.trim()
        if (c.equals(CORREO_PRUEBA, ignoreCase = true) && clave == CLAVE_PRUEBA) return true
        return registrado != null &&
                registrado.correo.isNotBlank() &&
                c.equals(registrado.correo.trim(), ignoreCase = true) &&
                clave == registrado.clave
    }
}
