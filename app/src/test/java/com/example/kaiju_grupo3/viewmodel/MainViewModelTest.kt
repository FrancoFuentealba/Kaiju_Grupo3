package com.example.kaiju_grupo3.viewmodel

import com.example.kaiju_grupo3.MainDispatcherRule
import com.example.kaiju_grupo3.navigation.NavigationEvent
import com.example.kaiju_grupo3.navigation.Screen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    // Escucha los eventos del ViewModel y los va guardando en una lista.
    private fun kotlinx.coroutines.test.TestScope.escuchar(vm: MainViewModel): List<NavigationEvent> {
        val eventos = mutableListOf<NavigationEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.navigationEvents.toList(eventos)
        }
        return eventos
    }

    @Test
    fun `navigateTo emite un evento NavigateTo con los parametros`() = runTest {
        val vm = MainViewModel()
        val eventos = escuchar(vm)

        vm.navigateTo(Screen.Inventario, popUpTo = Screen.Login, inclusive = true, singleTop = true)

        assertEquals(
            NavigationEvent.NavigateTo(Screen.Inventario, Screen.Login, inclusive = true, singleTop = true),
            eventos.single()
        )
    }

    @Test
    fun `navigateTo sin opciones usa los valores por defecto`() = runTest {
        val vm = MainViewModel()
        val eventos = escuchar(vm)

        vm.navigateTo(Screen.Detalle(5))

        assertEquals(NavigationEvent.NavigateTo(Screen.Detalle(5)), eventos.single())
    }

    @Test
    fun `navigationBack emite PopBackStack`() = runTest {
        val vm = MainViewModel()
        val eventos = escuchar(vm)

        vm.navigationBack()

        assertEquals(NavigationEvent.PopBackStack, eventos.single())
    }

    @Test
    fun `navigationUp emite NavigateUp`() = runTest {
        val vm = MainViewModel()
        val eventos = escuchar(vm)

        vm.navigationUp()

        assertEquals(NavigationEvent.NavigateUp, eventos.single())
    }

    @Test
    fun `los eventos llegan en el orden en que se emitieron`() = runTest {
        val vm = MainViewModel()
        val eventos = escuchar(vm)

        vm.navigateTo(Screen.Resumen)
        vm.navigationBack()

        assertEquals(2, eventos.size)
        assertEquals(NavigationEvent.PopBackStack, eventos.last())
    }
}