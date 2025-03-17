package com.example.tourmate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tourmate.Database.AppDatabase
import com.example.tourmate.Database.DatabaseCopyHelper
import com.example.tourmate.managers.SharedPreferencesManager.SharedPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SharedPreferencesManager(this)
        val appDatabase = AppDatabase.getInstance(this)
        lifecycleScope.launch(Dispatchers.IO) {
            val cities = appDatabase.citiesDao().getAllCities()
        }


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
}