package com.example.simplebankingapp.presentation.activities.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplebankingapp.data.model.Result
import com.example.simplebankingapp.data.model.Transaction
import com.example.simplebankingapp.data.model.TransferRequest
import com.example.simplebankingapp.data.model.TransferResponse
import com.example.simplebankingapp.domain.entity.Account
import com.example.simplebankingapp.domain.repository.TransactionRepository
import com.example.simplebankingapp.domain.usecases.FundTransferUseCase
import com.example.simplebankingapp.domain.usecases.GetAccountsUseCase
import com.example.simplebankingapp.presentation.fragments.transaction.TransactionListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class TransferUiState {
    object Idle : TransferUiState()
    object LoadingAccounts : TransferUiState()
    data class AccountsLoaded(val accounts: List<Account>) : TransferUiState()
    object LoadingTransfer : TransferUiState()
    data class TransferSuccess(val transferResponse: TransferResponse) : TransferUiState()
    data class TransferError(val message: String) : TransferUiState()
    object NoAccountsFound : TransferUiState()
}
class TransferViewModel(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val transferUseCase: FundTransferUseCase
) : ViewModel(){
    private val _uiState = MutableStateFlow<TransferUiState>(TransferUiState.Idle)
    val uiState: StateFlow<TransferUiState> = _uiState.asStateFlow()

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()

    private val _selectedAccount = MutableStateFlow<Account?>(null)
    val selectedAccount: StateFlow<Account?> = _selectedAccount.asStateFlow()

    init {
        fetchAccounts()
    }

    fun fetchAccounts() {
        viewModelScope.launch {
            _uiState.value = TransferUiState.LoadingAccounts
            when (val result = getAccountsUseCase(0, 50)) {
                is Result.Success -> {
                    if (result.data.content.isNotEmpty()) {
                        _accounts.value = result.data.content
                        _uiState.value = TransferUiState.AccountsLoaded(result.data.content)
                        _selectedAccount.value = result.data.content.first()
                    } else {
                        _uiState.value = TransferUiState.NoAccountsFound
                    }
                }
                is Result.Error -> {
                    _uiState.value = TransferUiState.TransferError("Failed to load accounts: ${result.exception.message}")
                }
            }
        }
    }


    fun transfer(transferRequest: TransferRequest){
        viewModelScope.launch {
            val result = transferUseCase(transferRequest)
            _uiState.value = TransferUiState.LoadingTransfer
            when(result){
                is Result.Success -> {
                    _uiState.value = TransferUiState.TransferSuccess(result.data)
                }
                is Result.Error -> {
                    _uiState.value = TransferUiState.TransferError(result.exception.message ?: "Unknown Error")
                }
            }
        }
    }


    fun selectAccount(account: Account) {
        if (_selectedAccount.value?.id != account.id) {
            _selectedAccount.value = account
        }
    }

}