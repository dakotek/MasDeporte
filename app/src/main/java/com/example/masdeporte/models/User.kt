package com.example.masdeporte.models

data class User(
    val userId: String,
    val name: String,
    val email: String,
    val userType: UserType
)

enum class UserType {
    STANDARD,
    ADMIN
}