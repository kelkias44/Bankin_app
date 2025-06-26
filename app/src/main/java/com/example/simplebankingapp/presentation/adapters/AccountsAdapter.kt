package com.example.simplebankingapp.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simplebankingapp.R
import com.example.simplebankingapp.domain.entity.Account

class AccountCarouselAdapter(private val accounts: List<Account>) :
    RecyclerView.Adapter<AccountCarouselAdapter.AccountViewHolder>() {

    // ViewHolder for the account_card layout
    class AccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val typeTv: TextView = itemView.findViewById(R.id.type_tv)
        val accountNumberTv: TextView = itemView.findViewById(R.id.account_number_tv)
        val accountBalanceTitleTv: TextView = itemView.findViewById(R.id.account_balance_title_tv) // Not directly used for text, but good to keep
        val accountBalanceTv: TextView = itemView.findViewById(R.id.account_balance_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        // Inflate the account_card layout for each item in the carousel
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.account_card, parent, false)
        return AccountViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val account = accounts[position]

        // Bind data to the TextViews in the account card
        holder.typeTv.text = account.accountType // Set account type (e.g., SAVINGS, CHECKING)
        // Mask account number to show only the last 4 digits
        val maskedAccountNumber = "**** **** **** " + account.accountNumber.takeLast(4)
        holder.accountNumberTv.text = maskedAccountNumber
        // Format balance as currency (e.g., "$1250.75")
        holder.accountBalanceTv.text = "$${"%.2f".format(account.balance)}"
    }

    override fun getItemCount(): Int = accounts.size // Return the total number of accounts
}