package com.example.simplebankingapp.presentation.fragments.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.example.simplebankingapp.data.model.Result
import com.example.simplebankingapp.domain.entity.Account
import com.example.simplebankingapp.domain.usecases.GetAccountsUseCase

class AccountViewModel(private val getAccountsUseCase: GetAccountsUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow<AccountListUiState>(AccountListUiState.Loading)
    val uiState: StateFlow<AccountListUiState> = _uiState

    init {
        fetchAccounts()
    }
    fun fetchAccounts(initialPage: Int = 0, pageSize: Int = 10) {
        viewModelScope.launch {
            _uiState.value = AccountListUiState.Loading

            val allAccounts = mutableListOf<Account>()
            var currentPage = initialPage
            var hasMorePages = true

            while (hasMorePages) {
                when (val result = getAccountsUseCase(currentPage, pageSize)) {
                    is Result.Success -> {
                        val response = result.data
                        allAccounts.addAll(response.content)
                        hasMorePages = !response.last && response.content.isNotEmpty()
                        if (hasMorePages) {
                            currentPage++
                        }
                    }
                    is Result.Error -> {
                        _uiState.value = AccountListUiState.Error(result.exception.message ?: "An unknown error occurred")
                        hasMorePages = false
                    }
                }
            }

            if (_uiState.value !is AccountListUiState.Error) {
                if (allAccounts.isNotEmpty()) {
                    _uiState.value = AccountListUiState.Success(allAccounts)
                } else {
                    _uiState.value = AccountListUiState.Error("No accounts found.")
                }
            }
        }
    }
}

sealed class AccountListUiState {
    object Loading : AccountListUiState()
    data class Success(val accounts: List<Account>) : AccountListUiState()
    data class Error(val message: String) : AccountListUiState()
}