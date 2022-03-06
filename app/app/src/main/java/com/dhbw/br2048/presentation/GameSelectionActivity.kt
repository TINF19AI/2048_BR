package com.dhbw.br2048.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import com.dhbw.br2048.R
import com.dhbw.br2048.api.SocketHandler
import com.dhbw.br2048.data.Constants
import com.dhbw.br2048.data.toLobby
import com.dhbw.br2048.databinding.ActivityGameSelectionBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject

class GameSelectionActivity : BaseActivity() {

    private lateinit var b: ActivityGameSelectionBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("GameSelectionActivity", "Activity created")

        b = ActivityGameSelectionBinding.inflate(layoutInflater)
        setContentView(b.root)

        setToolbar(b.abTop)

        b.btCreateLobby.setOnClickListener {
            Log.d("GameSelection", "Create Lobby was clicked")

            if (!SocketHandler.getSocket().connected()) {
                Log.d("LobbyList", "no connection")
                runOnUiThread {
                    Snackbar.make(
                        b.btCreateLobby,
                        getString(R.string.no_connection),
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                }
                return@setOnClickListener
            }


            SocketHandler.request(Constants.SOCK_NEW_GAME, null) {
                val lobby = (it[0] as JSONObject).toLobby()
                runOnUiThread {
                    val lobbyIntent = Intent(this, LobbyActivity::class.java)
                    lobbyIntent.putExtra(Constants.BUNDLE_KEY_GAMEID, lobby.id)
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
            gameIntent.putExtra(Constants.BUNDLE_KEY_GAMEID, "") // empty gameID for singleplayer
            startActivity(gameIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_br, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onHelpPressed() {
        MaterialAlertDialogBuilder(
            b.root.context,
            R.style.AlertDialogRegular
        ).setMessage(getString(R.string.battle_royale_rules))
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}