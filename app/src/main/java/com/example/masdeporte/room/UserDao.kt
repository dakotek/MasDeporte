package com.example.masdeporte.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object (DAO) para la entidad de usuario.
 */
@Dao
interface UserDao {
    /**
     * Inserta un usuario en la base de datos. Si ya existe, lo reemplaza.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    /**
     * Obtiene el primer usuario almacenado en la base de datos.
     * @return El usuario almacenado o null si no hay usuarios.
     */
    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getUser(): UserEntity?

    /**
     * Elimina todos los usuarios de la base de datos.
     */
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}
