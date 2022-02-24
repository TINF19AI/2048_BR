package com.dhbw.br2048.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.br2048.R
import com.dhbw.br2048.api.GameSocket
import com.dhbw.br2048.data.User
import com.dhbw.br2048.data.toLobby
import com.dhbw.br2048.databinding.ActivityLobbyBinding
import org.json.JSONObject

class LobbyActivity : BaseActivity() {
    private lateinit var b: ActivityLobbyBinding
    private var gameSocket: GameSocket? = null

    private var gameId: String = ""
    private var keepSocket: Boolean = false

    // For Lobby RecyclerView
    private val userList: MutableList<User> = mutableListOf()
    private lateinit var userAdapter: UserAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityLobbyBinding.inflate(layoutInflater)
        setContentView(b.root)
        setToolbar(b.abTop)

        b.btStartGame.setOnClickListener {
            gameSocket?.startGame()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        b.rvUsers.layoutManager = LinearLayoutManager(b.root.context, RecyclerView.VERTICAL, false)
        userAdapter = UserAdapter(userList)
        b.rvUsers.adapter = userAdapter

        intent.extras?.getString("gameID")?.let {
            gameId = it // set lobby id
            gameSocket = GameSocket(
                it,
                getUserId()
            ) { list, _ ->
                Log.d("Lobby", "received user names")
                userList.clear()
                for (user in list) {
                    userList.add(User(user.username)) // add usernames to recyclerview
                }

                runOnUiThread {
                    userAdapter.notifyDataSetChanged()
                    Log.d("Lobby", "user count " + userList.size.toString())
                }
            }
        }

        gameSocket?.socket?.on("start") {
            Log.d("Lobby", "Received start signal for game: $gameId")
            runOnUiThread {
                keepSocket = true
                val lobbyIntent = Intent(this, GameActivity::class.java)
                lobbyIntent.putExtra("gameID", gameId)
                startActivity(lobbyIntent)
                finish() // stop activity to prevent adding it to backstack
            }
        }

        gameSocket?.socket?.on("lobbyDetails") { jsonLobby ->
            Log.d("Lobby", "Received lobbyDetails for game: $gameId")
            val lobby = (jsonLobby[0] as JSONObject).toLobby()
            runOnUiThread {
                // hide button if not owner
                if (lobby.owner != getUserId()) {
                    b.btStartGame.visibility = View.GONE
                }

                "${resources.getString(R.string.players)} (${lobby.currentUsers} / ${lobby.maxUsers})".also { b.tvUsers.text = it }
            }
        }
        gameSocket?.lobbyDetails()

        Log.d("Lobby", "Joined Lobby: " + gameId)
        b.lobbyID.text = "ID: $gameId"
    }

    override fun onStop() {
        if (!keepSocket) {
            gameSocket?.close()
        }
        Log.d("LobbyActivity", "onStop")
        super.onStop()
    }
}
