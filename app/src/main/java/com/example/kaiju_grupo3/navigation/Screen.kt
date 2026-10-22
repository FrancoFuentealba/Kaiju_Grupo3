package com.example.kaiju_grupo3.navigation

// sealed class para rutas seguras en la navegación
sealed class Screen(val route: String) {

    // --- Acceso ---
    data object Login : Screen(route = "login")
    data object Registro : Screen(route = "registro")
    data object Resumen : Screen(route = "resumen")

    // --- Pestañas principales (barra inferior) ---
    data object Inventario : Screen(route = "inventario")
    data object Movimientos : Screen(route = "movimientos")
    data object Profile : Screen(route = "profile_page")

    // --- Otras pantallas ---
    data object Settings : Screen(route = "settings_page")

    // --- Formularios ---
    data object ProductoNuevo : Screen(route = "producto_nuevo")

    data class ProductoEditar(val productoId: Long) : Screen(route = "producto_editar/$productoId") {
        companion object {
            const val ARG_ID = "productoId"
            const val RUTA = "producto_editar/{$ARG_ID}"
        }
    }

    data class MovimientoNuevo(val productoId: Long) : Screen(route = "movimiento_nuevo/$productoId") {
        companion object {
            const val ARG_ID = "productoId"
            const val RUTA = "movimiento_nuevo/{$ARG_ID}"
        }
    }

    // Pantalla con argumento: Screen.Detalle(5) navega a "producto/5".
    data class Detalle(val productoId: Long) : Screen(route = "producto/$productoId") {
        companion object {
            const val ARG_ID = "productoId"
            const val RUTA = "producto/{$ARG_ID}"   // patrón que se declara en el NavHost
        }
    }
}

