package com.example.masdeporte.models

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