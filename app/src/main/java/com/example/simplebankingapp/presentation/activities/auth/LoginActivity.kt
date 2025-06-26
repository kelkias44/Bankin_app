package com.example.simplebankingapp.presentation.activities.auth

import LoginViewModel
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.simplebankingapp.databinding.ActivityLoginBinding
import com.example.simplebankingapp.domain.entity.AuthResponse
import com.example.simplebankingapp.presentation.activities.MainActivity
import com.example.simplebankingapp.utils.SharedPreferencesManager

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.btnLogin.setOnClickListener {
            val username = binding.usernameTv.text.toString()
            val password = binding.passwordTv.text.toString()

            if (username.isBlank() || password.isBlank()) {
                showMessage("Username and password are required")
                return@setOnClickListener
            }

            viewModel.login(username, password)
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is LoginViewModel.UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                }
                is LoginViewModel.UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    saveTokens(state.authResponse)
                    showMessage(state.authResponse.message)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is LoginViewModel.UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    showMessage(state.message)
                }
                else -> Unit
            }
        }
    }

    private fun saveTokens(authResponse: AuthResponse) {
        val sharedPref = SharedPreferencesManager(this)
        sharedPref.login(authResponse)
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}