package com.example.kaiju_grupo3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kaiju_grupo3.model.ValorIndicador
import com.example.kaiju_grupo3.repository.DolarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DolarViewModel : ViewModel() {

    private val repository = DolarRepository()

    private val _dolar = MutableStateFlow<ValorIndicador?>(null)
    val dolar: StateFlow<ValorIndicador?> = _dolar.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        cargarDolar()
    }

    fun cargarDolar() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                _dolar.value = repository.obtenerDolarHoy()
                if (_dolar.value == null) _error.value = "No hay valor del dólar disponible"
            } catch (e: Exception) {
                _error.value = "No se pudo obtener el precio del dólar"
            }
            _cargando.value = false
        }
    }
}