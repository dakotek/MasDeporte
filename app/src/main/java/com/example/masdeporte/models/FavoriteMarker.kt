package com.example.masdeporte.models

/**
 * Clase de datos que representa los marcadores favoritos de un usuario.
 *
 * @property userEmail Correo electrónico del usuario al que pertenecen los marcadores favoritos.
 * @property markers Lista de identificadores de marcadores favoritos.
 */
data class FavoriteMarker(
    val userEmail: String = "",
    val markers: List<String> = emptyList()
)