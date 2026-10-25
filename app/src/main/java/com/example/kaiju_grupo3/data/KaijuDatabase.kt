package com.example.kaiju_grupo3.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kaiju_grupo3.model.Movimiento
import com.example.kaiju_grupo3.model.Producto

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
