package com.example.mycloset.activities

import android.content.Intent
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
import com.example.mycloset.api.controllers.AuthController
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

        if (!AuthController.instance.isLoggedIn) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_navhost) as NavHostFragment?
        if (navHostFragment != null) {
            navController = navHostFragment.navController
        }

        val navView = findViewById<BottomNavigationView>(R.id.main_bottomNavigationView)
        setupWithNavController(navView, navController)

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.postListFragment, R.id.searchFragment, R.id.createPostFragment,
                R.id.likedPostsFragment, R.id.myProfileFragment -> {
                    navController.popBackStack(R.id.postListFragment, true)
                    navController.navigate(item.itemId)
                    true
                }

                else -> false
            }
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val topLevelDestinations: MutableSet<Int> = HashSet()
        topLevelDestinations.add(R.id.postListFragment)
        topLevelDestinations.add(R.id.searchFragment)
        topLevelDestinations.add(R.id.createPostFragment)
        topLevelDestinations.add(R.id.likedPostsFragment)
        topLevelDestinations.add(R.id.myProfileFragment)

        val appBarConfiguration: AppBarConfiguration =
            AppBarConfiguration.Builder(topLevelDestinations).build()
        setupActionBarWithNavController(this, navController, appBarConfiguration)
    }
}