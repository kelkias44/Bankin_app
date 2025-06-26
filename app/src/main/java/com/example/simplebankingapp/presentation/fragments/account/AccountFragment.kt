package com.example.simplebankingapp.presentation.fragments.account

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ProgressBar // For the loading indicator
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.simplebankingapp.R
import com.example.simplebankingapp.domain.entity.Account
import com.example.simplebankingapp.domain.entity.AccountResponse
import com.example.simplebankingapp.domain.entity.Pageable
import com.example.simplebankingapp.domain.entity.Sort
import com.example.simplebankingapp.domain.repository.AccountRepository
import com.example.simplebankingapp.domain.usecases.GetAccountsUseCase
import com.example.simplebankingapp.presentation.fragments.account.AccountListUiState
import com.example.simplebankingapp.presentation.fragments.account.AccountViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest // collectLatest is good for UI updates as it cancels previous emissions
import kotlinx.coroutines.launch
import com.example.simplebankingapp.data.model.Result
import com.example.simplebankingapp.data.repository.AccountRepositoryImpl
import com.example.simplebankingapp.presentation.adapters.AccountCarouselAdapter
import com.google.android.material.button.MaterialButton

class AccountFragment : Fragment() {

    private lateinit var viewModel: AccountViewModel
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var errorTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)


        viewPager = view.findViewById(R.id.account_view_pager)
        tabLayout = view.findViewById(R.id.account_tab_indicator)
        loadingIndicator = view.findViewById(R.id.loading_indicator)
        errorTextView = view.findViewById(R.id.error_text_view)

        val accountRepository = AccountRepositoryImpl(requireContext())
        val getAccountsUseCase = GetAccountsUseCase(accountRepository)
        viewModel = ViewModelProvider(this, AccountViewModelFactory(getAccountsUseCase)).get(
            AccountViewModel::class.java
        )

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { uiState ->
                when (uiState) {
                    is AccountListUiState.Loading -> {
                        loadingIndicator.visibility = View.VISIBLE
                        errorTextView.visibility = View.GONE
                        viewPager.visibility = View.GONE
                        tabLayout.visibility = View.GONE
                    }

                    is AccountListUiState.Success -> {
                        loadingIndicator.visibility = View.GONE
                        errorTextView.visibility = View.GONE
                        viewPager.visibility = View.VISIBLE
                        tabLayout.visibility = View.VISIBLE
                        setupViewPager(uiState.accounts)
                    }

                    is AccountListUiState.Error -> {
                        loadingIndicator.visibility = View.GONE
                        viewPager.visibility = View.GONE
                        tabLayout.visibility = View.GONE
                        errorTextView.visibility = View.VISIBLE
                        errorTextView.text = uiState.message
                    }
                }
            }
        }
    }

    private fun setupViewPager(accounts: List<Account>) {
        val adapter = AccountCarouselAdapter(accounts)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
        }.attach()
    }
}

class AccountViewModelFactory(private val getAccountsUseCase: GetAccountsUseCase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") // Suppress unchecked cast warning
            return AccountViewModel(getAccountsUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
