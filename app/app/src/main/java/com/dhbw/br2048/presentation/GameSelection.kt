package com.dhbw.br2048.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dhbw.br2048.databinding.ActivityGameSelectionBinding

class GameSelection : AppCompatActivity() {

    private lateinit var b: ActivityGameSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityGameSelectionBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btCreateLobby.setOnClickListener {
            Log.d("GameSelection", "Create Lobby was clicked")
        }

    }
}