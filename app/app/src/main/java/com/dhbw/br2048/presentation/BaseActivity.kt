package com.dhbw.br2048.presentation

import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.dhbw.br2048.R

// open keyword is to make BaseActivity inheritable
open class BaseActivity : AppCompatActivity() {

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
        val userName = sp.getString("username", "Slider")

        userName?.let {
            return it
        } ?: run {
            return "Slider"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                Log.d("BaseActivity", "back button pressed")
                finish()
            }
            else -> {
                Log.d("BaseActivity", "unknown button pressed: " + item.itemId.toString())
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun setToolbar(toolbar: Toolbar) {
        // Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

}