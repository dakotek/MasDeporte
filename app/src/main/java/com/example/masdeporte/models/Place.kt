package com.example.masdeporte.models

/**
 * Clase de datos que representa un lugar deportivo.
 *
 * @property title Nombre del lugar.
 * @property sport Deporte practicado en el lugar.
 * @property description Descripción del lugar.
 * @property rating Valoración del lugar (número de estrellas).
 * @property latitude Latitud geográfica del lugar.
 * @property longitude Longitud geográfica del lugar.
 * @property addedByUserEmail Correo electrónico del usuario que añadió el lugar.
 * @property accepted Indica si el lugar ha sido aceptado por un administrador.
 */
data class Place(
    val title: String,
    val sport: String,
    val description: String,
    val rating: Int,
    val latitude: Double,
    val longitude: Double,
    val addedByUserEmail: String,
    val accepted: Boolean,
)