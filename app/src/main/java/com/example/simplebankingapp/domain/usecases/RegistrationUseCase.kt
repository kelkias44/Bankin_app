package com.example.simplebankingapp.domain.usecases

import android.util.Patterns
import com.example.simplebankingapp.domain.entity.AuthResponse
import com.example.simplebankingapp.domain.entity.RegistrationResponse
import com.example.simplebankingapp.domain.repository.AuthRepository

class RegistrationUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String
    ): Result<RegistrationResponse> {
        if (username.isBlank() || password.isBlank() || firstName.isBlank() ||
            lastName.isBlank() || phoneNumber.isBlank()) {
            return Result.failure(IllegalArgumentException("All fields are required"))
        }



        if (!phoneNumber.isValidPhone()) {
            return Result.failure(IllegalArgumentException("Invalid phone number"))
        }
        if(password.length < 6){
            return Result.failure(IllegalArgumentException("Password must be minimum of 6 digits"))
        }

        return authRepository.register(
            username, password, firstName, lastName, phoneNumber
        )
    }
}

fun String.isValidEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPhone(): Boolean {
    return Patterns.PHONE.matcher(this).matches() && length >= 10
}
