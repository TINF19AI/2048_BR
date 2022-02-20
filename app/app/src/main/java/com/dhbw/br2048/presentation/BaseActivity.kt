package com.dhbw.br2048.presentation

import android.os.Bundle
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

// not needed because App is restarted after theme change
//    override fun onResume() {
//        val sp = getSharedPreferences("theme", MODE_PRIVATE)
//        val tempThemeId = sp.getInt("currentTheme", R.style.Theme_Original)
//        if (currentThemeId != tempThemeId) {
//            this.recreate() // recreate activity if theme has changed
//        }
//        super.onResume()
//    }
}