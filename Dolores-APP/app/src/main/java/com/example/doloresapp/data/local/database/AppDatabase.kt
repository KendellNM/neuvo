package com.example.doloresapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.doloresapp.data.local.dao.*
import com.example.doloresapp.data.local.entity.*

@Database(
    entities = [
        ProductoEntity::class,
        PedidoEntity::class,
        NotificacionEntity::class,
        RecetaDigitalEntity::class,
        UbicacionDeliveryEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun productoDao(): ProductoDao
    abstract fun pedidoDao(): PedidoDao
    abstract fun notificacionDao(): NotificacionDao
    abstract fun recetaDigitalDao(): RecetaDigitalDao
    abstract fun ubicacionDeliveryDao(): UbicacionDeliveryDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dolores_farmacia_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
