package com.gvugar.tourmate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.gvugar.tourmate.Database.AppDatabase
import com.gvugar.tourmate.Database.DatabaseCopyHelper
import com.gvugar.tourmate.managers.SharedPreferencesManager.SharedPreferencesManager
import com.gvugar.tourmate.retrofit.dao.MessagesRoomDao
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() , NavigationListener {
private lateinit var  bottomNavigationView:BottomNavigationView
private lateinit var messagesRoomDao: MessagesRoomDao

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

//    override fun onDestroy() {
//        super.onDestroy()
//        lifecycleScope.launch {
//            messagesRoomDao.deleteAllMessages()
//        }
//    }
}