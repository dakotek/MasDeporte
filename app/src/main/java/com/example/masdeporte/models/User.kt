package com.example.masdeporte.models

data class User(
    val id:String?,
    val userId: String,
    val name: String,
    val userType: String
) {
    fun toMap(): MutableMap<String, Any>{
        return mutableMapOf(
            "user_id" to this.userId,
            "name" to this.name,
            "userType" to this.userType
        )
    }
}