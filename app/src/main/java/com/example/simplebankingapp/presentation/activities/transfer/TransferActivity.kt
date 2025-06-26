package com.example.simplebankingapp.presentation.activities.transfer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.transition.Visibility
import com.example.simplebankingapp.R
import com.example.simplebankingapp.data.model.TransferRequest
import com.example.simplebankingapp.data.repository.AccountRepositoryImpl
import com.example.simplebankingapp.data.repository.TransactionRepositoryImpl
import com.example.simplebankingapp.domain.entity.Account
import com.example.simplebankingapp.domain.usecases.FundTransferUseCase
import com.example.simplebankingapp.domain.usecases.GetAccountsUseCase
import com.example.simplebankingapp.domain.usecases.GetTransactionsUseCase
import com.example.simplebankingapp.presentation.activities.MainActivity
import com.example.simplebankingapp.presentation.fragments.transaction.TransactionFragment.AccountSpinnerAdapter
import com.example.simplebankingapp.presentation.fragments.transaction.TransactionFragment.TransactionViewModelFactory
import com.example.simplebankingapp.presentation.fragments.transaction.TransactionViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.collectLatest

class TransferActivity : AppCompatActivity() {
    private lateinit var viewModel: TransferViewModel
    private lateinit var accountSpinner: Spinner
    private lateinit var toAccountEditText: TextInputEditText
    private lateinit var amountEditText: TextInputEditText
    private lateinit var toAccountLayout: TextInputLayout
    private lateinit var amountLayout: TextInputLayout
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transfer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        accountSpinner = findViewById(R.id.account_spinner)
        toAccountEditText = findViewById(R.id.toAccountEditText)
        amountEditText = findViewById(R.id.amountEditText)
        toAccountLayout = findViewById(R.id.toAccountInputLayout)
        amountLayout = findViewById(R.id.amountInputLayout)
        submitButton = findViewById(R.id.submitButton)

        submitButton.setOnClickListener {
            validateInputs()
        }
        setupViewModel()

    }


    private fun setupViewModel(){
        val accountRepository = AccountRepositoryImpl(this)
        val getAccountsUseCase = GetAccountsUseCase(accountRepository)

        val transactionRepository = TransactionRepositoryImpl(this)
        val fundTransferUseCase = FundTransferUseCase(transactionRepository)

        viewModel = ViewModelProvider(
            this,
            TransferViewModelFactory(getAccountsUseCase, fundTransferUseCase)
        ).get(TransferViewModel::class.java)


        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collectLatest { uiState ->
                when (uiState) {
                    is TransferUiState.AccountsLoaded -> {
                        setupAccountSpinner(uiState.accounts)
                    }
                    is TransferUiState.TransferSuccess -> {
                        Toast.makeText(this@TransferActivity, "Successfully Transfered", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@TransferActivity,MainActivity::class.java))
                        finish()
                    }
                    is TransferUiState.LoadingTransfer -> {
                        submitButton.visibility = View.GONE
                    }
                    is TransferUiState.TransferError -> {
                        submitButton.visibility = View.VISIBLE
                        Toast.makeText(this@TransferActivity, uiState.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {

                    }
                }


                }
            }
    }


    private fun setupAccountSpinner(accounts: List<Account>) {
        val adapter = AccountSpinnerAdapter(this, accounts)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        accountSpinner.adapter = adapter

        viewModel.selectedAccount.value?.let { selectedAcc ->
            val initialPosition = accounts.indexOfFirst { it.id == selectedAcc.id }
            if (initialPosition != -1) {
                accountSpinner.setSelection(initialPosition)
            }
        }

        accountSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedAccount = parent?.getItemAtPosition(position) as Account
                viewModel.selectAccount(selectedAccount)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun validateInputs() {
        val toAccount = toAccountEditText.text?.toString()?.trim()
        val amount = amountEditText.text?.toString()?.trim()

        var isValid = true

        if(viewModel.selectedAccount.value == null){
            Toast.makeText(this, "Select Sender Account", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (toAccount.isNullOrEmpty()) {
            toAccountLayout.error = "To Account is required"
            isValid = false
        } else {
            toAccountLayout.error = null
        }

        if (amount.isNullOrEmpty()) {
            amountLayout.error = "Amount is required"
            isValid = false
        } else {
            val amountValue = amount.toDoubleOrNull()
            if (amountValue == null || amountValue <= 0.0) {
                amountLayout.error = "Enter a valid amount"
                isValid = false
            } else {
                amountLayout.error = null
            }
        }

        if (isValid) {
            viewModel.transfer(TransferRequest(
                fromAccountNumber = viewModel.selectedAccount.value!!.accountNumber,
                toAccountNumber = toAccountEditText.text.toString(),
                amount = amount!!.toDouble()
            ))
        }
    }



}


class TransferViewModelFactory(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val transactionUseCase: FundTransferUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransferViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransferViewModel(getAccountsUseCase, transactionUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
