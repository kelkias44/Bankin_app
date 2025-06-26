package com.example.simplebankingapp.presentation.fragments.transaction

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplebankingapp.R
import com.example.simplebankingapp.domain.entity.Account
import com.example.simplebankingapp.data.repository.AccountRepositoryImpl
import com.example.simplebankingapp.data.repository.TransactionRepositoryImpl
import com.example.simplebankingapp.domain.usecases.GetAccountsUseCase
import com.example.simplebankingapp.domain.usecases.GetTransactionsUseCase
import com.example.simplebankingapp.presentation.adapters.TransactionAdapter
import kotlinx.coroutines.flow.collectLatest

class TransactionFragment : Fragment() {

    private lateinit var viewModel: TransactionViewModel
    private lateinit var accountSpinner: Spinner
    private lateinit var transactionsRecyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var errorTextView: TextView
    private lateinit var noTransactionsTextView: TextView
    private lateinit var loadMoreProgressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transaction, container, false)

        accountSpinner = view.findViewById(R.id.account_spinner)
        transactionsRecyclerView = view.findViewById(R.id.transactions_recycler_view)
        loadingIndicator = view.findViewById(R.id.loading_indicator)
        errorTextView = view.findViewById(R.id.error_text_view)
        noTransactionsTextView = view.findViewById(R.id.no_transactions_text_view)
        loadMoreProgressBar = view.findViewById(R.id.load_more_progress_bar)

        setupViewModel()
        setupRecyclerView()
        setupListeners()

        return view
    }

    private fun setupViewModel() {

        val accountRepository = AccountRepositoryImpl(requireContext())
        val getAccountsUseCase = GetAccountsUseCase(accountRepository)


        val transactionRepository = TransactionRepositoryImpl(requireContext())
        val getTransactionsUseCase = GetTransactionsUseCase(transactionRepository)

        viewModel = ViewModelProvider(
            this,
            TransactionViewModelFactory(getAccountsUseCase, getTransactionsUseCase)
        ).get(TransactionViewModel::class.java)

        // --- Observe UI State ---
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collectLatest { uiState ->
                when (uiState) {
                    TransactionListUiState.Idle, TransactionListUiState.LoadingAccounts -> {
                        loadingIndicator.visibility = View.VISIBLE
                        errorTextView.visibility = View.GONE
                        accountSpinner.visibility = View.GONE
                        transactionsRecyclerView.visibility = View.GONE
                        noTransactionsTextView.visibility = View.GONE
                        loadMoreProgressBar.visibility = View.GONE
                    }
                    is TransactionListUiState.AccountsLoaded -> {
                        loadingIndicator.visibility = View.GONE
                        errorTextView.visibility = View.GONE
                        accountSpinner.visibility = View.VISIBLE
                        // Populate spinner once accounts are loaded
                        setupAccountSpinner(uiState.accounts)
                    }
                    TransactionListUiState.LoadingTransactions -> {
                        loadingIndicator.visibility = View.VISIBLE
                        transactionsRecyclerView.visibility = View.GONE
                        noTransactionsTextView.visibility = View.GONE
                        loadMoreProgressBar.visibility = View.GONE
                        errorTextView.visibility = View.GONE
                    }
                    is TransactionListUiState.TransactionsLoaded -> {
                        loadingIndicator.visibility = View.GONE
                        loadMoreProgressBar.visibility = View.GONE
                        errorTextView.visibility = View.GONE
                        if (uiState.transactions.isEmpty()) {
                            transactionsRecyclerView.visibility = View.GONE
                            noTransactionsTextView.visibility = View.VISIBLE
                        } else {
                            noTransactionsTextView.visibility = View.GONE
                            transactionsRecyclerView.visibility = View.VISIBLE
                            transactionAdapter.submitList(uiState.transactions)
                        }
                    }
                    TransactionListUiState.LoadingMoreTransactions -> {
                        loadMoreProgressBar.visibility = View.VISIBLE
                        errorTextView.visibility = View.GONE
                    }
                    is TransactionListUiState.Error -> {
                        loadingIndicator.visibility = View.GONE
                        loadMoreProgressBar.visibility = View.GONE
                        transactionsRecyclerView.visibility = View.GONE
                        accountSpinner.visibility = View.GONE
                        noTransactionsTextView.visibility = View.GONE
                        errorTextView.visibility = View.VISIBLE
                        errorTextView.text = uiState.message
                    }
                    TransactionListUiState.NoAccountsFound -> {
                        loadingIndicator.visibility = View.GONE
                        loadMoreProgressBar.visibility = View.GONE
                        transactionsRecyclerView.visibility = View.GONE
                        accountSpinner.visibility = View.GONE
                        errorTextView.visibility = View.VISIBLE
                        errorTextView.text = "No accounts found for this user."
                    }
                }
            }
        }
    }

    private fun setupAccountSpinner(accounts: List<Account>) {
        val adapter = AccountSpinnerAdapter(requireContext(), accounts)
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

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter()
        transactionsRecyclerView.layoutManager = LinearLayoutManager(context)
        transactionsRecyclerView.adapter = transactionAdapter

        // Infinite scroll listener
        transactionsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                // Check if we're near the end of the list and not already loading
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    viewModel.loadNextPage()
                }
            }
        })
    }

    private fun setupListeners() {
        // No specific listeners beyond spinner and recycler view in this example
    }


    // --- AccountSpinnerAdapter for the dropdown ---
    class AccountSpinnerAdapter(context: Context, private val accounts: List<Account>) :
        ArrayAdapter<Account>(context, android.R.layout.simple_spinner_item, accounts) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return createItemView(position, convertView, parent)
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            return createItemView(position, convertView, parent)
        }

        private fun createItemView(position: Int, convertView: View?, parent: ViewGroup): View {
            val account = getItem(position)
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item_account, parent, false)

            val accountTypeText: TextView = view.findViewById(R.id.account_type_text)
            val accountNumberText: TextView = view.findViewById(R.id.account_number_text)

            accountTypeText.text = account?.accountType
            accountNumberText.text = "**** **** **** ${account?.accountNumber?.takeLast(4)}"

            return view
        }
    }



    class TransactionViewModelFactory(
        private val getAccountsUseCase: GetAccountsUseCase,
        private val getTransactionsUseCase: GetTransactionsUseCase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TransactionViewModel(getAccountsUseCase, getTransactionsUseCase) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
