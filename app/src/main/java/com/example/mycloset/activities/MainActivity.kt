package com.example.mycloset.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.mycloset.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_navhost) as NavHostFragment?
        if (navHostFragment != null) {
            navController = navHostFragment.navController
        }

        val navView = findViewById<BottomNavigationView>(R.id.main_bottomNavigationView)
        setupWithNavController(navView, navController)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val appBarConfiguration: AppBarConfiguration = AppBarConfiguration.Builder().build()
        setupActionBarWithNavController(this, navController, appBarConfiguration)
    }
}