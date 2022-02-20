package com.dhbw.br2048.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.dhbw.br2048.api.SocketHandler
import com.dhbw.br2048.data.toLobby
import com.dhbw.br2048.databinding.ActivityGameSelectionBinding
import org.json.JSONObject

class GameSelectionActivity : BaseActivity() {

    private lateinit var b: ActivityGameSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityGameSelectionBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btCreateLobby.setOnClickListener {
            Log.d("GameSelection", "Create Lobby was clicked")
            SocketHandler.request("newGame", null) {
                val lobby = (it[0] as JSONObject).toLobby()
                runOnUiThread(Runnable {
                    val lobbyIntent = Intent(this, LobbyActivity::class.java)
                    lobbyIntent.putExtra("gameID", lobby.id)
                    startActivity(lobbyIntent)
                })
            }
        }

        b.btJoinLobby.setOnClickListener {
            val gameIntent = Intent(this, LobbyListActivity::class.java)
            startActivity(gameIntent)
        }

        b.btSinglePlayer.setOnClickListener {
            val gameIntent = Intent(this, GameActivity::class.java)
            gameIntent.putExtra("gameID", "")
            startActivity(gameIntent)
        }
    }
}