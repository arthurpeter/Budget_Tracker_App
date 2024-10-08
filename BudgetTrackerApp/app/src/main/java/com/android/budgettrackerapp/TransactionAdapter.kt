package com.android.budgettrackerapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class TransactionAdapter(private var transactions: List<Transaction>):
    RecyclerView.Adapter<TransactionAdapter.CategoryHolder>() {

    class CategoryHolder(view: View): RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.label)
        val amount: TextView = view.findViewById(R.id.amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_layout, parent, false)
        return CategoryHolder(view)
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        val transaction = transactions[position]
        val context = holder.amount.context

        if (transaction.amount >= 0) {
            holder.amount.text = "+ %.2f ${transaction.currency}".format(transaction.amount)
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.green2))
        } else {
            holder.amount.text = "- %.2f ${transaction.currency}".format(abs(transaction.amount))
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.red2))
        }

        holder.label.text = transaction.label

        holder.itemView.setOnClickListener {
            context.changeActivity(DetailedActivity::class.java,
                "transaction" to transaction)
        }
    }

    fun setData(transactions: List<Transaction>) {
        this.transactions = transactions
        notifyDataSetChanged()
    }
}