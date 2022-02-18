package com.dhbw.br2048.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dhbw.br2048.api.GameSocket
import com.dhbw.br2048.databinding.ActivityLobbyBinding

class LobbyActivity : AppCompatActivity() {
    private lateinit var b: ActivityLobbyBinding
    private var gameSocket: GameSocket? = null

    var gameId: String = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityLobbyBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btStartGame.setOnClickListener {
            gameSocket?.startGame()
        }

        intent.extras?.getString("gameID")?.let {
            gameId = it // set lobby id
            gameSocket = GameSocket(
                it,
                Settings.Global.getString(baseContext.contentResolver, "device_name")
            ) { list, position ->
                // TODO: Show players in lobby
            }
        }

        gameSocket?.socket?.on("start") {
            Log.d("Lobby", "Received start signal for game: " + gameId)
            runOnUiThread(Runnable {
                val lobbyIntent = Intent(this, GameActivity::class.java)
                lobbyIntent.putExtra("gameID", gameId)
                startActivity(lobbyIntent)
            })
        }

        Log.d("Lobby", "Joined Lobby: " + gameId)
        b.lobbyID.text = "ID: $gameId"
    }
}