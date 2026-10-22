package com.example.kaiju_grupo3.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kaiju_grupo3.data.FotoStorage
import com.example.kaiju_grupo3.data.KaijuDatabase
import com.example.kaiju_grupo3.model.Producto
import com.example.kaiju_grupo3.model.ProductoFormEstado
import com.example.kaiju_grupo3.model.ValidacionesProducto
import com.example.kaiju_grupo3.repository.CodigoDuplicadoException
import com.example.kaiju_grupo3.repository.InventarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Formulario de alta y edición de producto. Mismo patrón que UsuarioViewModel:
//  - un estado (StateFlow) con lo que escribe el usuario y los errores de cada campo
//  - funciones onXChange que guardan el valor y borran el error de ese campo
//  - validarFormulario() que revisa todo y devuelve true/false
class ProductoFormViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = InventarioRepository(KaijuDatabase.obtener(application))

    private val _estado = MutableStateFlow(ProductoFormEstado())
    val estado: StateFlow<ProductoFormEstado> = _estado.asStateFlow()

    private var productoOriginal: Producto? = null   // producto que se está editando (null si es nuevo)
    private var fotoGuardada: String? = null         // foto que ya está guardada en la base de datos
    private var guardado = false

    // ----- Carga (solo al editar) -----
    fun cargarProducto(id: Long) {
        if (productoOriginal != null || _estado.value.cargando) return   // ya se cargó
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true) }
            val p = repositorio.obtenerProducto(id)
            if (p == null) {
                _estado.update {
                    it.copy(cargando = false, errores = it.errores.copy(general = "El producto no existe"))
                }
                return@launch
            }
            productoOriginal = p
            fotoGuardada = p.fotoUri
            _estado.value = ProductoFormEstado(
                id = p.id,
                codigo = p.codigo,
                nombre = p.nombre,
                descripcion = p.descripcion,
                categoria = p.categoria,
                tipo = p.tipo,
                precio = p.precio.toString(),
                stockMinimo = p.stockMinimo.toString(),
                talla = p.talla.orEmpty(),
                color = p.color.orEmpty(),
                modelo = p.modelo.orEmpty(),
                lote = p.lote.orEmpty(),
                vencimiento = p.vencimiento.orEmpty(),
                fotoUri = p.fotoUri
            )
        }
    }

    // ----- Cada vez que el usuario escribe: se guarda el valor y se borra el error de ese campo -----
    fun onCodigoChange(valor: String) =
        _estado.update { it.copy(codigo = valor.take(20).uppercase(), errores = it.errores.copy(codigo = null)) }

    fun onNombreChange(valor: String) =
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }

    fun onDescripcionChange(valor: String) =
        _estado.update { it.copy(descripcion = valor, errores = it.errores.copy(descripcion = null)) }

    fun onCategoriaChange(valor: String) =
        _estado.update { it.copy(categoria = valor, errores = it.errores.copy(categoria = null)) }

    fun onTipoChange(valor: String) =
        _estado.update { it.copy(tipo = valor) }

    // En los campos numéricos solo se aceptan dígitos (máximo 8 para no desbordar un Int).
    fun onPrecioChange(valor: String) =
        _estado.update { it.copy(precio = valor.filter(Char::isDigit).take(8), errores = it.errores.copy(precio = null)) }

    fun onStockInicialChange(valor: String) =
        _estado.update { it.copy(stockInicial = valor.filter(Char::isDigit).take(7), errores = it.errores.copy(stockInicial = null)) }

    fun onStockMinimoChange(valor: String) =
        _estado.update { it.copy(stockMinimo = valor.filter(Char::isDigit).take(7), errores = it.errores.copy(stockMinimo = null)) }

    fun onTallaChange(valor: String) =
        _estado.update { it.copy(talla = valor, errores = it.errores.copy(talla = null)) }

    fun onColorChange(valor: String) =
        _estado.update { it.copy(color = valor, errores = it.errores.copy(color = null)) }

    fun onModeloChange(valor: String) =
        _estado.update { it.copy(modelo = valor, errores = it.errores.copy(modelo = null)) }

    fun onLoteChange(valor: String) =
        _estado.update { it.copy(lote = valor, errores = it.errores.copy(lote = null)) }

    fun onVencimientoChange(valor: String) =
        _estado.update { it.copy(vencimiento = valor.take(10), errores = it.errores.copy(vencimiento = null)) }

    // ----- Foto (guía 13) -----

    // La cámara ya dejó la foto en la Uri que se le entregó: solo hay que usarla.
    fun onFotoDesdeCamara(uri: Uri) = reemplazarFoto(uri.toString())

    // La galería entrega una Uri temporal: primero se copia a la carpeta de la app.
    fun onFotoDesdeGaleria(uri: Uri) {
        viewModelScope.launch {
            val copia = FotoStorage.copiarDesdeGaleria(getApplication<Application>(), uri)
            if (copia != null) {
                reemplazarFoto(copia.toString())
            } else {
                _estado.update {
                    it.copy(errores = it.errores.copy(general = "No se pudo cargar la imagen seleccionada"))
                }
            }
        }
    }

    fun quitarFoto() = reemplazarFoto(null)

    private fun reemplazarFoto(nueva: String?) {
        val anterior = _estado.value.fotoUri
        // Si la foto anterior era una de este formulario (aún no guardada), se borra del almacenamiento.
        if (anterior != null && anterior != fotoGuardada && anterior != nueva) {
            FotoStorage.eliminar(getApplication<Application>(), anterior)
        }
        _estado.update { it.copy(fotoUri = nueva, errores = it.errores.copy(general = null)) }
    }

    // ----- Validación y guardado -----

    // Revisa todo el formulario. Devuelve true si está bien, false si hay errores.
    fun validarFormulario(): Boolean {
        val errores = ValidacionesProducto.validar(_estado.value)
        _estado.update { it.copy(errores = errores) }
        return !errores.hayErrores()
    }

    fun guardar(onExito: () -> Unit) {
        if (_estado.value.guardando || !validarFormulario()) return
        val s = _estado.value
        viewModelScope.launch {
            _estado.update { it.copy(guardando = true) }
            val resultado = if (s.esNuevo) {
                repositorio.crearProducto(construirProducto(s), s.stockInicial.trim().toInt())
            } else {
                repositorio.guardarProducto(construirProducto(s))
            }
            resultado
                .onSuccess {
                    guardado = true
                    // Si se cambió o quitó la foto de un producto existente, se borra la foto vieja.
                    if (fotoGuardada != null && fotoGuardada != s.fotoUri) {
                        FotoStorage.eliminar(getApplication<Application>(), fotoGuardada)
                    }
                    onExito()
                }
                .onFailure { error ->
                    _estado.update {
                        if (error is CodigoDuplicadoException) {
                            it.copy(guardando = false, errores = it.errores.copy(codigo = error.message))
                        } else {
                            it.copy(
                                guardando = false,
                                errores = it.errores.copy(general = "No se pudo guardar el producto. Inténtalo de nuevo.")
                            )
                        }
                    }
                }
        }
    }

    // Convierte el texto del formulario en un Producto (ya validado). Los opcionales vacíos quedan en null.
    private fun construirProducto(s: ProductoFormEstado) = Producto(
        id = s.id,
        codigo = s.codigo.trim(),
        nombre = s.nombre.trim(),
        descripcion = s.descripcion.trim(),
        categoria = s.categoria.trim(),
        tipo = s.tipo,
        precio = s.precio.trim().toInt(),
        stock = productoOriginal?.stock ?: 0,
        stockMinimo = s.stockMinimo.trim().toInt(),
        talla = s.talla.trim().ifBlank { null },
        color = s.color.trim().ifBlank { null },
        modelo = s.modelo.trim().ifBlank { null },
        lote = s.lote.trim().ifBlank { null },
        vencimiento = s.vencimiento.trim().ifBlank { null },
        fotoUri = s.fotoUri
    )

    // Si el usuario sale sin guardar, se borra la foto nueva que quedó huérfana.
    override fun onCleared() {
        if (!guardado) {
            val actual = _estado.value.fotoUri
            if (actual != null && actual != fotoGuardada) {
                FotoStorage.eliminar(getApplication<Application>(), actual)
            }
        }
        super.onCleared()
    }
}
