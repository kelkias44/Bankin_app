package com.example.simplebankingapp.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.simplebankingapp.R
import com.example.simplebankingapp.databinding.ActivityMainBinding
import com.example.simplebankingapp.presentation.activities.auth.LoginActivity
import com.example.simplebankingapp.utils.SharedPreferencesManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAuthState()
    }

    private fun checkAuthState() {
        val sharedPref = SharedPreferencesManager(this)
        val isLoggedIn = sharedPref.isLoggedIn()


        if (!isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            setupUI()
        }
    }

    private fun setupUI() {
        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                as? NavHostFragment ?: return
        navController = navHostFragment.navController

        binding.bottomNav.apply {
            setupWithNavController(navController)
            itemIconTintList = null
            labelVisibilityMode = BottomNavigationView.LABEL_VISIBILITY_LABELED
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


    private fun logout() {
        val sharedPref = SharedPreferencesManager(this)
        sharedPref.onLogout()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}