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
            val gameID = "my-game-id"
            SocketHandler.request("newGame", gameID){
                Log.d("newGame", it.toString())
                runOnUiThread(Runnable {
                    val gameIntent = Intent(this, GameActivity::class.java)
                    gameIntent.putExtra("gameID", gameID)
                    startActivity(gameIntent)
                })
            }
        }

        b.btSinglePlayer.setOnClickListener {
            val gameIntent = Intent(this, GameActivity::class.java)
            gameIntent.putExtra("gameID", "")
            startActivity(gameIntent)
        }

        SocketHandler.request("getGames", null){
            Log.d("getGames", it.toString())
            runOnUiThread(Runnable {
                //@todo show lobby list
            })
        }

    }
}