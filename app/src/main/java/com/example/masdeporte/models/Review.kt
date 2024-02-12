package com.example.masdeporte.models

/**
 * Clase de datos que representa una reseña de un marcador.
 *
 * @property markerId Identificador del marcador al que pertenece la reseña.
 * @property review Texto de la reseña.
 * @property userEmail Correo electrónico del usuario que escribió la reseña.
 */
data class Review(
    val markerId: String,
    val review: String,
    val userEmail: String,
)