package com.example.kaiju_grupo3.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kaiju_grupo3.data.KaijuDatabase
import com.example.kaiju_grupo3.model.MovimientoFormEstado
import com.example.kaiju_grupo3.model.TipoMovimiento
import com.example.kaiju_grupo3.model.ValidacionesMovimiento
import com.example.kaiju_grupo3.repository.InventarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Formulario para registrar un movimiento (entrada, salida, venta o ajuste) de un producto.
class MovimientoFormViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = InventarioRepository(KaijuDatabase.obtener(application))

    private val _estado = MutableStateFlow(MovimientoFormEstado())
    val estado: StateFlow<MovimientoFormEstado> = _estado.asStateFlow()

    private var productoCargado: Long? = null

    // Carga el producto y sigue observándolo (si su stock cambia, el formulario se actualiza).
    fun cargarProducto(id: Long) {
        if (productoCargado == id) return
        productoCargado = id
        viewModelScope.launch {
            repositorio.observarProducto(id).collect { producto ->
                _estado.update { it.copy(producto = producto) }
            }
        }
    }

    fun onTipoChange(tipo: TipoMovimiento) =
        _estado.update { it.copy(tipo = tipo, errores = it.errores.copy(cantidad = null)) }

    // Solo dígitos (máximo 7 para no desbordar un Int).
    fun onCantidadChange(valor: String) =
        _estado.update { it.copy(cantidad = valor.filter(Char::isDigit).take(7), errores = it.errores.copy(cantidad = null)) }

    fun onNotaChange(valor: String) =
        _estado.update { it.copy(nota = valor, errores = it.errores.copy(nota = null)) }

    // Revisa el formulario. Devuelve true si está bien, false si hay errores.
    fun validarFormulario(): Boolean {
        val s = _estado.value
        val errores = ValidacionesMovimiento.validar(s.producto, s.tipo, s.cantidad, s.nota)
        _estado.update { it.copy(errores = errores) }
        return !errores.hayErrores()
    }

    fun guardar(onExito: () -> Unit) {
        if (_estado.value.guardando || !validarFormulario()) return
        val s = _estado.value
        val producto = s.producto ?: return
        viewModelScope.launch {
            _estado.update { it.copy(guardando = true) }
            repositorio
                .registrarMovimiento(producto.id, s.tipo, s.cantidad.trim().toInt(), s.nota.trim())
                .onSuccess { onExito() }
                .onFailure { error ->
                    _estado.update {
                        it.copy(
                            guardando = false,
                            errores = it.errores.copy(general = error.message ?: "No se pudo registrar el movimiento")
                        )
                    }
                }
        }
    }
}