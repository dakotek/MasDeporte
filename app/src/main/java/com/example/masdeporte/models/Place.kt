package com.example.masdeporte.models

data class Place(
    val placeId: String,
    val name: String,
    val location: Location,
    val category: Category,
    val comment: String,
    val rating: Float,
    val addedByUserId: String
)

data class Location(
    val latitude: Double,
    val longitude: Double
)

enum class Category {
    SKATE,
    FUTBOL,
    PARKOUR,
    BALONCESTO,
    ESCALADA,
    BICI
}
