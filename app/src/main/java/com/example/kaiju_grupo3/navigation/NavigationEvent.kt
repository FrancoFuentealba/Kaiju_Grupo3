package com.example.kaiju_grupo3.navigation

//representa los difernetes eventos de navegacion
sealed class NavigationEvent {
    //evento para poder navegar
    data class NavigateTo(
        val route: Screen,
        val popUpToRoute: Screen? = null,
        val inclusive: Boolean = false,
        val singleTop: Boolean = false
    ): NavigationEvent()

    //evento para volver a la pantalla anterior
    object PopBackStack : NavigationEvent()

    //evento para navegar hacia "arriba" o deslisar hacia arriva.
    object NavigateUp : NavigationEvent()
}