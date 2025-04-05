package com.example.tourmate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.tourmate.Database.AppDatabase
import com.example.tourmate.Database.DatabaseCopyHelper
import com.example.tourmate.managers.SharedPreferencesManager.SharedPreferencesManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() , NavigationListener {
private lateinit var  bottomNavigationView:BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottomNav)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.navController)

        SharedPreferencesManager(this)
        AppDatabase.getInstance(this)

        copyDatabase()

    }

    fun copyDatabase(){
        val db = DatabaseCopyHelper(this)
        try {
            db.createDatabase()
        }catch (e:Exception){
            e.printStackTrace()
        }

        try {
            db.openDatabase()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun changeBottomMenuVisibility(visibility: Int) {
        bottomNavigationView.visibility = visibility
    }
}