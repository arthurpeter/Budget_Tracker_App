package com.android.budgettrackerapp

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.android.budgettrackerapp.databinding.ActivityAddTransactionBinding
import com.android.budgettrackerapp.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTransactionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.main.setOnClickListener {
            this.window.decorView.clearFocus()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        if (!intent.getBooleanExtra("purchase", true)) {
            binding.addTransactionBtn.text = "Add to budget"
        } else {
            binding.addTransactionBtn.text = "Add expense"
        }

        binding.labelInput.addTextChangedListener {
            if (it!!.isNotEmpty()) {
                binding.labelLayout.error = null
            }
        }

        binding.amountInput.addTextChangedListener {
            if (it!!.isNotEmpty()) {
                binding.amountLayout.error = null
            }
        }

        binding.addTransactionBtn.setOnClickListener {
            val label = binding.labelInput.text.toString()
            var amount = binding.amountInput.text.toString().toDoubleOrNull()
            val description = binding.descriptionInput.text.toString()

            if (label.isEmpty()) {
                binding.labelLayout.error = "Please enter a valid label"
            } else if (amount == null) {
                binding.amountLayout.error = "Please enter a valid amount"
            } else {
                if (intent.getBooleanExtra("purchase", true)) {
                    amount = -amount
                }
                val transaction = Transaction(0, label, amount, description)
                insert(transaction)
            }
        }

        binding.closeBtn.setOnClickListener {
            finish()
        }
    }

    private fun insert(transaction: Transaction) {
        val db = Room.databaseBuilder(this, AppDatabase::class.java,
            "transactions").build()

        GlobalScope.launch {
            db.transactionDao().insertAll(transaction)
            finish()
        }
    }
}