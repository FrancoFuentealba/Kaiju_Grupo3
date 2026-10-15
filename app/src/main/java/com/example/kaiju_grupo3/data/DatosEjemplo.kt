
package com.example.kaiju_grupo3.data

import com.example.kaiju_grupo3.model.Producto
import com.example.kaiju_grupo3.model.TipoMovimiento

// Catálogo y movimientos FICTICIOS para probar la app
data class MovimientoEjemplo(
    val diasAtras: Int,
    val tipo: TipoMovimiento,
    val cantidad: Int,
    val nota: String = ""
)

data class ProductoEjemplo(
    val producto: Producto,
    val movimientos: List<MovimientoEjemplo>   // en orden (del más antiguo al más nuevo)
)

object DatosEjemplo {

    fun catalogo(): List<ProductoEjemplo> = listOf(
        ProductoEjemplo(
            Producto(
                codigo = "KJ-0001", nombre = "Polera algodón básica", categoria = "Ropa",
                tipo = "Producto terminado", precio = 9990, stockMinimo = 10,
                descripcion = "Polera de algodón de corte regular.", talla = "M", color = "Negro"
            ),
            listOf(
                MovimientoEjemplo(30, TipoMovimiento.ENTRADA, 40, "Stock inicial"),
                MovimientoEjemplo(20, TipoMovimiento.VENTA, 12),
                MovimientoEjemplo(9, TipoMovimiento.VENTA, 9),
                MovimientoEjemplo(3, TipoMovimiento.VENTA, 10)
            )
        ),
        ProductoEjemplo(
            Producto(
                codigo = "KJ-0002", nombre = "Polerón con capucha", categoria = "Ropa",
                tipo = "Producto terminado", precio = 24990, stockMinimo = 6,
                descripcion = "Polerón de polar con bolsillo canguro.", talla = "L", color = "Gris"
            ),
            listOf(
                MovimientoEjemplo(28, TipoMovimiento.ENTRADA, 20, "Stock inicial"),
                MovimientoEjemplo(15, TipoMovimiento.VENTA, 5),
                MovimientoEjemplo(6, TipoMovimiento.VENTA, 4)
            )
        ),
        ProductoEjemplo(
            Producto(
                codigo = "KJ-0003", nombre = "Zapatillas urbanas", categoria = "Calzado",
                tipo = "Producto terminado", precio = 39990, stockMinimo = 4,
                descripcion = "Zapatillas livianas para uso diario.", talla = "42", modelo = "Runner X"
            ),
            listOf(
                MovimientoEjemplo(25, TipoMovimiento.ENTRADA, 15, "Stock inicial"),
                MovimientoEjemplo(12, TipoMovimiento.VENTA, 6),
                MovimientoEjemplo(4, TipoMovimiento.VENTA, 5)
            )
        ),
        ProductoEjemplo(
            Producto(
                codigo = "KJ-0004", nombre = "Gorra ajustable", categoria = "Accesorios",
                tipo = "Producto terminado", precio = 8990, stockMinimo = 8,
                descripcion = "Gorra con cierre ajustable.", color = "Azul"
            ),
            listOf(
                MovimientoEjemplo(22, TipoMovimiento.ENTRADA, 30, "Stock inicial"),
                MovimientoEjemplo(10, TipoMovimiento.VENTA, 8),
                MovimientoEjemplo(5, TipoMovimiento.VENTA, 10)
            )
        ),
        ProductoEjemplo(
            Producto(
                codigo = "KJ-0005", nombre = "Mochila 20 L", categoria = "Accesorios",
                tipo = "Producto terminado", precio = 19990, stockMinimo = 5,
                descripcion = "Mochila resistente al agua.", modelo = "Trek 20", color = "Verde"
            ),
            listOf(
                MovimientoEjemplo(27, TipoMovimiento.ENTRADA, 12, "Stock inicial"),
                MovimientoEjemplo(14, TipoMovimiento.VENTA, 3),
                MovimientoEjemplo(2, TipoMovimiento.AJUSTE, 8, "Conteo físico: faltaba 1 unidad")
            )
        ),
        ProductoEjemplo(
            Producto(
                codigo = "KJ-0006", nombre = "Jugo natural 500 ml", categoria = "Alimentos",
                tipo = "Perecible", precio = 1490, stockMinimo = 24,
                descripcion = "Jugo de frutas sin azúcar añadida.", lote = "L-2410", vencimiento = "2026-12-15"
            ),
            listOf(
                MovimientoEjemplo(18, TipoMovimiento.ENTRADA, 120, "Stock inicial"),
                MovimientoEjemplo(8, TipoMovimiento.VENTA, 50),
                MovimientoEjemplo(2, TipoMovimiento.VENTA, 40)
            )
        ),
        ProductoEjemplo(
            Producto(
                codigo = "KJ-0007", nombre = "Barra de cereal", categoria = "Alimentos",
                tipo = "Perecible", precio = 990, stockMinimo = 30,
                descripcion = "Barra de cereal con avena y miel.", lote = "L-2409", vencimiento = "2027-01-20"
            ),
            listOf(
                MovimientoEjemplo(20, TipoMovimiento.ENTRADA, 200, "Stock inicial"),
                MovimientoEjemplo(10, TipoMovimiento.VENTA, 80),
                MovimientoEjemplo(4, TipoMovimiento.VENTA, 60)
            )
        ),
        ProductoEjemplo(
            Producto(
                codigo = "KJ-0008", nombre = "Caja de cartón mediana", categoria = "Insumos",
                tipo = "Insumo", precio = 590, stockMinimo = 50,
                descripcion = "Caja para despachos de tamaño medio."
            ),
            listOf(
                MovimientoEjemplo(29, TipoMovimiento.ENTRADA, 300, "Stock inicial"),
                MovimientoEjemplo(16, TipoMovimiento.SALIDA, 120, "Consumo en despachos"),
                MovimientoEjemplo(7, TipoMovimiento.SALIDA, 100, "Consumo en despachos")
            )
        ),
        ProductoEjemplo(
            Producto(
                codigo = "KJ-0009", nombre = "Cinta de embalaje", categoria = "Insumos",
                tipo = "Insumo", precio = 1990, stockMinimo = 10,
                descripcion = "Cinta transparente de 48 mm."
            ),
            listOf(
                MovimientoEjemplo(29, TipoMovimiento.ENTRADA, 50, "Stock inicial"),
                MovimientoEjemplo(14, TipoMovimiento.SALIDA, 20, "Consumo en despachos"),
                MovimientoEjemplo(6, TipoMovimiento.SALIDA, 22, "Consumo en despachos")
            )
        ),
        ProductoEjemplo(
            Producto(
                codigo = "KJ-0010", nombre = "Etiquetas adhesivas (rollo)", categoria = "Insumos",
                tipo = "Insumo", precio = 3490, stockMinimo = 5,
                descripcion = "Rollo de 500 etiquetas para rotular productos."
            ),
            listOf(
                MovimientoEjemplo(26, TipoMovimiento.ENTRADA, 20, "Stock inicial"),
                MovimientoEjemplo(12, TipoMovimiento.SALIDA, 8, "Rotulado de productos")
            )
        ),
        ProductoEjemplo(
            Producto(
                codigo = "KJ-0011", nombre = "Audífonos inalámbricos", categoria = "Accesorios",
                tipo = "Producto terminado", precio = 29990, stockMinimo = 3,
                descripcion = "Audífonos bluetooth con estuche de carga.", modelo = "BT-200", color = "Negro"
            ),
            listOf(
                MovimientoEjemplo(24, TipoMovimiento.ENTRADA, 10, "Stock inicial"),
                MovimientoEjemplo(11, TipoMovimiento.VENTA, 4),
                MovimientoEjemplo(6, TipoMovimiento.VENTA, 3),
                MovimientoEjemplo(1, TipoMovimiento.VENTA, 2)
            )
        )
    )
}