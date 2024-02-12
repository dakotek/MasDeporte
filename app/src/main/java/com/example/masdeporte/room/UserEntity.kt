package com.example.masdeporte.room

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa a un usuario en la base de datos.
 * @param userId ID único del usuario.
 * @param name Nombre del usuario.
 * @param email Correo electrónico del usuario.
 * @param userType Tipo de usuario ("ADMIN" o "STANDARD").
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val userId: String,
    val name: String,
    val email: String,
    val userType: String
)
