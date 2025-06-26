package com.example.simplebankingapp.presentation.fragments.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplebankingapp.domain.entity.Account
import com.example.simplebankingapp.data.model.Result
import com.example.simplebankingapp.data.model.Transaction
import com.example.simplebankingapp.domain.usecases.GetAccountsUseCase
import com.example.simplebankingapp.domain.usecases.GetTransactionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class TransactionListUiState {
    object Idle : TransactionListUiState()
    object LoadingAccounts : TransactionListUiState()
    data class AccountsLoaded(val accounts: List<Account>) : TransactionListUiState()
    object LoadingTransactions : TransactionListUiState()
    data class TransactionsLoaded(val transactions: List<Transaction>, val hasMore: Boolean) : TransactionListUiState()
    object LoadingMoreTransactions : TransactionListUiState()
    data class Error(val message: String) : TransactionListUiState()
    object NoAccountsFound : TransactionListUiState()
}

class TransactionViewModel(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TransactionListUiState>(TransactionListUiState.Idle)
    val uiState: StateFlow<TransactionListUiState> = _uiState.asStateFlow()

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()

    private val _selectedAccount = MutableStateFlow<Account?>(null)
    val selectedAccount: StateFlow<Account?> = _selectedAccount.asStateFlow()

    private val _transactions = MutableStateFlow<MutableList<Transaction>>(mutableListOf())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private var currentPage = 0
    private val pageSize = 10
    private var isLastPage = false
    private var isLoadingMore = false

    init {
        fetchAccounts()
    }

    fun fetchAccounts() {
        viewModelScope.launch {
            _uiState.value = TransactionListUiState.LoadingAccounts
            when (val result = getAccountsUseCase(0, 50)) {
                is Result.Success -> {
                    if (result.data.content.isNotEmpty()) {
                        _accounts.value = result.data.content
                        _uiState.value = TransactionListUiState.AccountsLoaded(result.data.content)
                        _selectedAccount.value = result.data.content.first()
                        fetchTransactions(result.data.content.first().id, true)
                    } else {
                        _uiState.value = TransactionListUiState.NoAccountsFound
                    }
                }
                is Result.Error -> {
                    _uiState.value = TransactionListUiState.Error("Failed to load accounts: ${result.exception.message}")
                }
            }
        }
    }


    fun fetchTransactions(accountId: Int, isInitialFetch: Boolean) {
        if (isLoadingMore) return

        viewModelScope.launch {
            isLoadingMore = true

            if (isInitialFetch) {
                _uiState.value = TransactionListUiState.LoadingTransactions
                currentPage = 0
                isLastPage = false
                _transactions.value.clear()
            } else {
                if (isLastPage) {
                    isLoadingMore = false
                    return@launch
                }
                _uiState.value = TransactionListUiState.LoadingMoreTransactions
            }

            when (val result = getTransactionsUseCase(accountId, currentPage, pageSize)) {
                is Result.Success -> {
                    val response = result.data
                    val newTransactions = response.content

                    if(!newTransactions.isNullOrEmpty()){
                        if (isInitialFetch) {
                            _transactions.value = newTransactions.toMutableList()
                        } else {
                            val currentList = _transactions.value.toMutableList()
                            currentList.addAll(newTransactions)
                            _transactions.value = currentList
                        }
                    }

                    isLastPage = response.last
                    currentPage++

                    _uiState.value = TransactionListUiState.TransactionsLoaded(
                        transactions = _transactions.value.toList(),
                        hasMore = !isLastPage
                    )
                }
                is Result.Error -> {
                    _uiState.value = TransactionListUiState.Error("Failed to load transactions: ${result.exception.message}")
                }
            }
            isLoadingMore = false
        }
    }

    fun selectAccount(account: Account) {
        if (_selectedAccount.value?.id != account.id) {
            _selectedAccount.value = account
            fetchTransactions(account.id, true)
        }
    }

    fun loadNextPage() {
        _selectedAccount.value?.id?.let { accountId ->
            if (!isLoadingMore && !isLastPage) {
                fetchTransactions(accountId, false)
            }
        }
    }
}
