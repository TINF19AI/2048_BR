package com.dhbw.br2048.presentation

import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dhbw.br2048.R

// open keyword is to make BaseActivity inheritable
open class BaseActivity() : AppCompatActivity() {

    var currentThemeId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        // Theme from shared preferences
        // Author: Kai
        Log.d("BaseActivity", "Activity created")
        val sp = getSharedPreferences("theme", MODE_PRIVATE)
        currentThemeId = sp.getInt("currentTheme", R.style.Theme_Original)
        setTheme(currentThemeId)
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        val sp = getSharedPreferences("theme", MODE_PRIVATE)
        val tempThemeId = sp.getInt("currentTheme", R.style.Theme_Original)
        if (currentThemeId != tempThemeId) {
            this.recreate() // recreate activity if theme has changed
        }
        super.onResume()
    }

    fun getUserId(): String {
        val sp = getSharedPreferences("general", MODE_PRIVATE)
        val userId = sp.getString("userId", "")

        userId?.let {
            return it
        } ?: run {
            return ""
        }
    }

    fun getUsername(): String {
        val sp = getSharedPreferences("general", MODE_PRIVATE)
        val userName = sp.getString(
            "username",
            Settings.Global.getString(baseContext.contentResolver, "device_name")
        )

        userName?.let {
            return it
        } ?: run {
            return Settings.Global.getString(baseContext.contentResolver, "device_name")
        }
    }
}