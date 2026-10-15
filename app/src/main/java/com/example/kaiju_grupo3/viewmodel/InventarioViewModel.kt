package com.example.kaiju_grupo3.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kaiju_grupo3.data.KaijuDatabase
import com.example.kaiju_grupo3.model.Movimiento
import com.example.kaiju_grupo3.model.MovimientoConNombre
import com.example.kaiju_grupo3.model.Producto
import com.example.kaiju_grupo3.model.ResumenInventario
import com.example.kaiju_grupo3.model.stockBajo
import com.example.kaiju_grupo3.repository.InventarioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Estado del inventario para las pantallas. Extiende AndroidViewModel porque Room necesita el contexto.
class InventarioViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = InventarioRepository(KaijuDatabase.obtener(application))

    // ----- Estado de los filtros (lo que escribe/selecciona el usuario) -----
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private val _soloStockBajo = MutableStateFlow(false)
    val soloStockBajo: StateFlow<Boolean> = _soloStockBajo.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    // ----- Estado derivado de la base de datos -----

    // Lista de productos ya filtrada por búsqueda y por "solo stock bajo".
    val productos: StateFlow<List<Producto>> = combine(
        repositorio.productos, _busqueda, _soloStockBajo
    ) { lista, texto, soloBajo ->
        val consulta = texto.trim()
        lista.filter { p ->
            val coincide = consulta.isEmpty() ||
                    p.nombre.contains(consulta, ignoreCase = true) ||
                    p.codigo.contains(consulta, ignoreCase = true) ||
                    p.categoria.contains(consulta, ignoreCase = true)
            coincide && (!soloBajo || p.stockBajo)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Totales para la tarjeta de resumen (sobre todos los productos, sin filtros).
    val resumen: StateFlow<ResumenInventario> = repositorio.productos
        .map { lista ->
            ResumenInventario(
                totalProductos = lista.size,
                totalUnidades = lista.sumOf { it.stock },
                productosStockBajo = lista.count { it.stockBajo }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ResumenInventario())

    // Historial global de movimientos, con el nombre del producto de cada uno.
    val movimientos: StateFlow<List<MovimientoConNombre>> = combine(
        repositorio.movimientos, repositorio.productos
    ) { movs, prods ->
        val nombres = prods.associate { it.id to it.nombre }
        movs.map { MovimientoConNombre(it, nombres[it.productoId] ?: "Producto eliminado") }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        // Primera vez que se abre la app: carga el catálogo ficticio.
        viewModelScope.launch {
            repositorio.sembrarDatosDeEjemploSiVacio()
            _cargando.value = false
        }
    }

    // ----- Eventos desde la UI -----
    fun onBusquedaChange(valor: String) {
        _busqueda.value = valor
    }

    fun onSoloStockBajoChange(valor: Boolean) {
        _soloStockBajo.value = valor
    }

    // ----- Consultas para la pantalla de detalle -----
    fun producto(id: Long): Flow<Producto?> = repositorio.observarProducto(id)

    fun movimientosDe(id: Long): Flow<List<Movimiento>> = repositorio.observarMovimientosDe(id)
}
