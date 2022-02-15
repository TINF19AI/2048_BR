package com.dhbw.br2048.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dhbw.br2048.R
import com.dhbw.br2048.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val sp = getSharedPreferences("theme", MODE_PRIVATE)
        val spe = sp.edit()
        spe.putInt("currentTheme", R.style.Theme_Original)
        spe.apply()
        Log.d("GameActivity", "reset to default theme")

        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        startActivity(Intent(this, GameActivity::class.java))
//        startActivity(Intent(this, GameSelection::class.java))
    }
}