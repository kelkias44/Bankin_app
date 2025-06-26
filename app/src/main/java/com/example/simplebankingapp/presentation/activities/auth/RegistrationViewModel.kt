package com.example.simplebankingapp.presentation.activities.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.simplebankingapp.data.repository.AuthRepositoryImpl
import com.example.simplebankingapp.domain.usecases.RegistrationUseCase
import kotlinx.coroutines.launch

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {
    val context = application.applicationContext

    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState


    private val authRepository = AuthRepositoryImpl(context)
    private val registrationUseCase = RegistrationUseCase(authRepository)

    fun register(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = registrationUseCase(
                username, password, firstName, lastName, phoneNumber
            )

            _uiState.value = when {
                result.isSuccess -> {
                    UiState.Success(result.getOrNull()?.message ?: "")
                }
                else -> {
                    UiState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
                }
            }
        }
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val message: String) : UiState()
        data class Error(val message: String) : UiState()
    }
}