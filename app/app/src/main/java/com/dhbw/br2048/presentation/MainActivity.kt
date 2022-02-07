package com.dhbw.br2048.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.dhbw.br2048.R
import com.dhbw.br2048.data.Coordinates
import com.dhbw.br2048.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding

    private val gridFragment = GridFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)


        startActivity(Intent(this, GameActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
    }

}