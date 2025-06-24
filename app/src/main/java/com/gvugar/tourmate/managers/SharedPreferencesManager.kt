package com.gvugar.tourmate.managers

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesManager {
    val PREFS_NAME = "AppPrefs"

    lateinit var preferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    fun SharedPreferencesManager(context: Context){
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        editor = preferences.edit()
    }

    fun <T> setValue(key:String, value: T){
        when (value){
            is String -> editor.putString(key.toLowerCase(), value)
            is Int -> editor.putInt(key.toLowerCase(), value)
            is Boolean -> editor.putBoolean(key.toLowerCase(), value)
            is Float -> editor.putFloat(key.toLowerCase(), value)
            is Long -> editor.putLong(key.toLowerCase(), value)
        }
        editor.apply()
    }

    fun <T> getValue(key: String, defaultValue: T): T{
        when (defaultValue){
            is String -> return preferences.getString(key.toLowerCase(), defaultValue) as T
            is Int -> return preferences.getInt(key.toLowerCase(), defaultValue) as T
            is Boolean -> return preferences.getBoolean(key.toLowerCase(), defaultValue) as T
            is Float -> return preferences.getFloat(key.toLowerCase(), defaultValue) as T
            is Long -> return preferences.getLong(key.toLowerCase(), defaultValue) as T
        }
        return defaultValue
    }
}
