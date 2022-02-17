package com.dhbw.br2048.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dhbw.br2048.api.SocketHandler
import com.dhbw.br2048.databinding.ActivityGameSelectionBinding

class GameSelection : AppCompatActivity() {

    private lateinit var b: ActivityGameSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityGameSelectionBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btCreateLobby.setOnClickListener {
            Log.d("GameSelection", "Create Lobby was clicked")

            SocketHandler.request("newGame", "my-game-id"){
                Log.d("newGame", it.toString())
                runOnUiThread(Runnable {
                    startActivity(Intent(this, GameActivity::class.java))
                })
            }
        }

        SocketHandler.request("getGames", null){
            Log.d("getGames", it.toString())
            runOnUiThread(Runnable {
                //@todo show lobby list
            })
        }

    }
}