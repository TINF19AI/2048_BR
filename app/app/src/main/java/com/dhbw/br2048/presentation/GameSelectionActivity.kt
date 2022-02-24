package com.dhbw.br2048.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.dhbw.br2048.R
import com.dhbw.br2048.api.SocketHandler
import com.dhbw.br2048.data.toLobby
import com.dhbw.br2048.databinding.ActivityGameSelectionBinding
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject

class GameSelectionActivity : BaseActivity() {

    private lateinit var b: ActivityGameSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityGameSelectionBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Toolbar
        setSupportActionBar(b.abTop)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        b.btCreateLobby.setOnClickListener {
            Log.d("GameSelection", "Create Lobby was clicked")

            if (!SocketHandler.getSocket().connected()) {
                Log.d("LobbyList", "no connection")
                runOnUiThread {
                    Snackbar.make(b.btCreateLobby, R.string.no_connection, Snackbar.LENGTH_LONG)
                        .show()
                }
                return@setOnClickListener
            }


            SocketHandler.request("newGame", null) {
                val lobby = (it[0] as JSONObject).toLobby()
                runOnUiThread {
                    val lobbyIntent = Intent(this, LobbyActivity::class.java)
                    lobbyIntent.putExtra("gameID", lobby.id)
                    startActivity(lobbyIntent)
                }
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

}