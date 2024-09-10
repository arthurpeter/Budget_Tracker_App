package com.android.budgettrackerapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.budgettrackerapp.databinding.ActivityChangeCurrencyBinding
import com.android.budgettrackerapp.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.changeCurrencyBtn.setOnClickListener {
            binding.changeCurrencyBtn.isSelected = true
            changeActivity(LoadingActivity::class.java)
        }

        binding.closeBtn.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.changeCurrencyBtn.isSelected = false
    }
}