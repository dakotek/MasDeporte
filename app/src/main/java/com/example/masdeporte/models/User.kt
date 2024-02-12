package com.example.masdeporte.models

/**
 * Clase de datos que representa un usuario.
 *
 * @property id Identificador único del usuario (opcional).
 * @property userId Identificador único del usuario.
 * @property name Nombre del usuario.
 * @property userType Tipo de usuario ("ADMIN" o "STANDARD").
 */
data class User(
    val id:String?,
    val userId: String,
    val name: String,
    val userType: String
) {
    /**
     * Convierte el objeto User a un mapa mutable para su almacenamiento en Firestore.
     *
     * @return Mapa mutable que representa el objeto User.
     */
    fun toMap(): MutableMap<String, Any>{
        return mutableMapOf(
            "user_id" to this.userId,
            "name" to this.name,
            "userType" to this.userType
        )
    }
}