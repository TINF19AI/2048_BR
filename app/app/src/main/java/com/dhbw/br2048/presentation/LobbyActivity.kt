package com.dhbw.br2048.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Debug
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dhbw.br2048.api.GameSocket
import com.dhbw.br2048.data.toLobby
import com.dhbw.br2048.databinding.ActivityLobbyBinding
import org.json.JSONObject

class LobbyActivity : AppCompatActivity() {
    private lateinit var b: ActivityLobbyBinding
    private var gameSocket: GameSocket? = null

    var gameId: String = ""
    var keepSocket: Boolean = false

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
                runOnUiThread {
                    var userList = "Users: \n"
                    for (user in list){
                        userList += user.username + "\n"
                    }
                    b.lobbyUsers.text = userList
                }
            }
        }

        gameSocket?.socket?.on("start") {
            Log.d("Lobby", "Received start signal for game: " + gameId)
            runOnUiThread(Runnable {
                keepSocket = true
                val lobbyIntent = Intent(this, GameActivity::class.java)
                lobbyIntent.putExtra("gameID", gameId)
                startActivity(lobbyIntent)
            })
        }

        gameSocket?.socket?.on("lobbyDetails") {
            Log.d("Lobby", "Received lobbyDetails for game: " + gameId)
            val lobby = (it[0] as JSONObject).toLobby()
            runOnUiThread(Runnable {
                b.btStartGame.isEnabled = (lobby.owner == Settings.Global.getString(baseContext.contentResolver, "device_name"))
            })
        }
        gameSocket?.lobbyDetails()

        Log.d("Lobby", "Joined Lobby: " + gameId)
        b.lobbyID.text = "ID: $gameId"
    }

    override fun onStop() {
        if(!keepSocket){
            gameSocket?.close()
        }
        Log.d("LobbyActivity", "onStop")
        super.onStop()
    }
}