package com.dhbw.br2048.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
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

    var gameId: String = ""
    var keepSocket: Boolean = false

    // For Lobby RecyclerView
    private val userList: MutableList<User> = mutableListOf()
    private lateinit var userAdapter: UserAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityLobbyBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btStartGame.setOnClickListener {
            gameSocket?.startGame()
        }
    }

    override fun onResume() {
        super.onResume()

        b.rvUsers.layoutManager = LinearLayoutManager(b.root.context, RecyclerView.VERTICAL, false)
        userAdapter = UserAdapter(userList)
        b.rvUsers.adapter = userAdapter

        intent.extras?.getString("gameID")?.let {
            gameId = it // set lobby id
            gameSocket = GameSocket(
                it,
                Settings.Global.getString(baseContext.contentResolver, "device_name")
            ) { list, position ->
                Log.d("Lobby", "received user names")
                userList.clear()
                for (user in list) {
                    userList.add(User(user.username)) // add usernames to recyclerview
                }

                runOnUiThread(Runnable {
                    userAdapter.notifyDataSetChanged()
                    Log.d("Lobby", "user count " + userList.size.toString())
                })
            }
        }

        gameSocket?.socket?.on("start") {
            Log.d("Lobby", "Received start signal for game: " + gameId)
            runOnUiThread(Runnable {
                keepSocket = true
                val lobbyIntent = Intent(this, GameActivity::class.java)
                lobbyIntent.putExtra("gameID", gameId)
                startActivity(lobbyIntent)
                finish() // stop activity to prevent adding it to backstack
            })
        }

        gameSocket?.socket?.on("lobbyDetails") {
            Log.d("Lobby", "Received lobbyDetails for game: " + gameId)
            val lobby = (it[0] as JSONObject).toLobby()
            runOnUiThread(Runnable {
                // hide button if not owner
                if (!(lobby.owner == Settings.Global.getString(
                        baseContext.contentResolver,
                        "device_name"
                    ))
                ) {
                    b.btStartGame.visibility = View.GONE
                }

                b.tvUsers.text =
                    "${resources.getString(R.string.players)} (${lobby.currentUsers} / ${lobby.maxUsers})"
            })
        }
        gameSocket?.lobbyDetails()

        Log.d("Lobby", "Joined Lobby: " + gameId)
        b.lobbyID.text = gameId
    }

    override fun onStop() {
        if (!keepSocket) {
            gameSocket?.close()
        }
        Log.d("LobbyActivity", "onStop")
        super.onStop()
    }
}