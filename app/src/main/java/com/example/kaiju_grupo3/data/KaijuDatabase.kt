package com.example.kaiju_grupo3.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kaiju_grupo3.model.Movimiento
import com.example.kaiju_grupo3.model.Producto

// Base de datos local de la app (SQLite administrada por Room).
// Si cambian las tablas hay que subir "version" (o desinstalar la app durante el desarrollo).
@Database(
    entities = [Producto::class, Movimiento::class],
    version = 1,
    exportSchema = false
)
abstract class KaijuDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao
    abstract fun movimientoDao(): MovimientoDao

    companion object {
        @Volatile
        private var instancia: KaijuDatabase? = null

        // Singleton: toda la app usa la misma instancia de la base de datos.
        fun obtener(context: Context): KaijuDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    KaijuDatabase::class.java,
                    "kaiju.db"
                ).build().also { instancia = it }
            }
    }
}
