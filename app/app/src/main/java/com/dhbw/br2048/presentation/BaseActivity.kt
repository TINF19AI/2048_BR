package com.dhbw.br2048.presentation

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.dhbw.br2048.R
import com.dhbw.br2048.data.Constants

// open keyword is to make BaseActivity inheritable
open class BaseActivity : AppCompatActivity() {

    var currentThemeId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        // Theme from shared preferences
        // Author: Kai
        Log.d("BaseActivity", "Activity created")
        val sp = getSharedPreferences(Constants.SP_CAT_GENERAL, MODE_PRIVATE)
        currentThemeId = sp.getInt(Constants.SP_KEY_THEME, R.style.Theme_Original)
        setTheme(currentThemeId)
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        val sp = getSharedPreferences(Constants.SP_CAT_GENERAL, MODE_PRIVATE)
        val tempThemeId = sp.getInt(Constants.SP_KEY_THEME, R.style.Theme_Original)
        if (currentThemeId != tempThemeId) {
            this.recreate() // recreate activity if theme has changed
        }
        super.onResume()
    }

    fun getUserId(): String {
        val sp = getSharedPreferences(Constants.SP_CAT_GENERAL, MODE_PRIVATE)
        val userId = sp.getString(Constants.SP_KEY_USERID, "")

        userId?.let {
            return it
        } ?: run {
            return ""
        }
    }

    fun getUsername(): String {
        val sp = getSharedPreferences(Constants.SP_CAT_GENERAL, MODE_PRIVATE)
        val userName = sp.getString(Constants.SP_KEY_USERNAME, "Slider")

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