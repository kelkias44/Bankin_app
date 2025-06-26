package com.example.simplebankingapp.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simplebankingapp.R
import com.example.simplebankingapp.data.model.Transaction
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class TransactionAdapter :
    androidx.recyclerview.widget.ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(
        TransactionDiffCallback()
    ) {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val typeTv: TextView = itemView.findViewById(R.id.transaction_type_tv)
        val descriptionTv: TextView = itemView.findViewById(R.id.transaction_description_tv)
        val amountTv: TextView = itemView.findViewById(R.id.transaction_amount_tv)
        val dateTv: TextView = itemView.findViewById(R.id.transaction_date_tv)
        val direction: TextView = itemView.findViewById(R.id.transaction_direction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_card, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)

        holder.typeTv.text = transaction.type
        holder.descriptionTv.text = transaction.description
        holder.amountTv.text = "ETB ${transaction.amount}"
        holder.direction.text = transaction.direction

        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC") // Keep UTC if your timestamps are always UTC
        val date: Date? = formatter.parse(transaction.timestamp)

        if (date != null) {
            // You might want to format the date for display as well
            val displayFormatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            holder.dateTv.text = displayFormatter.format(date)
        }




    }

    private class TransactionDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}