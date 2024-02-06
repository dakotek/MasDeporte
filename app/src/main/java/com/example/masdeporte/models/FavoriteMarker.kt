package com.example.masdeporte.models

data class FavoriteMarker(
    val userEmail: String = "",
    val markers: List<String> = emptyList()
)