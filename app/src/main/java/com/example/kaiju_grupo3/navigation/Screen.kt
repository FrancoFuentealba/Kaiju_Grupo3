package com.example.kaiju_grupo3.navigation

//sealed class para rutas seguras en la navegacion
sealed class Screen(val route: String) {
    //ruta para pantalla de inicio
    data object Home : Screen(route = "home_page")
    //ruta para pantalla de perfil
    data object Profile : Screen(route = "profile_page")
    //ruta de pantalla configuracion
    data object Settings : Screen(route = "settings_page")
    //ruta para pantalla de inicio de sesion

    data object Login : Screen(route = "login")
    //ruta para pantalla de registro (formulario)

    data object Registro : Screen(route = "registro")
    //ruta para pantalla de resumen (datos ingresados)



    data object Resumen : Screen(route = "resumen")
}