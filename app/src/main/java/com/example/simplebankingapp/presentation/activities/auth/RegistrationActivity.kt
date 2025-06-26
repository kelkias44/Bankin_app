package com.example.simplebankingapp.presentation.activities.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.simplebankingapp.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding
    private val viewModel: RegistrationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.btnRegister.setOnClickListener {
            val username = binding.usernameTv.text.toString()
            val password = binding.passwordEditText.text.toString()
            val firstName = binding.firstNameTv.text.toString()
            val lastName = binding.lastNameTv.text.toString()
            val phoneNumber = binding.phoneNumberEditText.text.toString()

            viewModel.register(username, password, firstName, lastName, phoneNumber)
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is RegistrationViewModel.UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnRegister.isEnabled = false
                }
                is RegistrationViewModel.UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    showMessage(state.message)
                    finish()
                }
                is RegistrationViewModel.UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    showMessage(state.message)
                }
                else -> Unit
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}