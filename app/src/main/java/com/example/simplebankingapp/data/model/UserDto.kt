package com.example.simplebankingapp.data.model

import com.example.simplebankingapp.domain.entity.User

data class UserDto(
    val username: String,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phoneNumber: String?
) {
    fun toUser(): User {
        return User(
            username = username,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber
        )
    }
}