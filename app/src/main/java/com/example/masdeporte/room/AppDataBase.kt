package com.example.masdeporte.room

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Clase de base de datos para la aplicación.
 */
@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Retorna el objeto de acceso a datos (DAO) para la entidad de usuario.
     */
    abstract fun userDao(): UserDao
}
