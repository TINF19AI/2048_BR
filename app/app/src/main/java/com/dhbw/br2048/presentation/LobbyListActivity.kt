package com.dhbw.br2048.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.br2048.api.SocketHandler
import com.dhbw.br2048.api.SocketHandler.emit
import com.dhbw.br2048.api.SocketHandler.getSocket
import com.dhbw.br2048.data.Lobby
import com.dhbw.br2048.data.toLobby
import com.dhbw.br2048.databinding.ActivityLobbyListBinding
import org.json.JSONArray
import org.json.JSONObject

class LobbyListActivity : BaseActivity() {

    private lateinit var b: ActivityLobbyListBinding

    // For Lobby RecyclerView
    private val lobbyList: MutableList<Lobby> = mutableListOf()
    private lateinit var lobbyAdapter: LobbyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityLobbyListBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btCreateLobby.setOnClickListener {
            SocketHandler.request("newGame", null) {
                val lobby = (it[0] as JSONObject).toLobby()
                runOnUiThread(Runnable {
                    val lobbyIntent = Intent(this, LobbyActivity::class.java)
                    lobbyIntent.putExtra("gameID", lobby.id)
                    startActivity(lobbyIntent)
                    finish() // keep this from backstack
                })
            }
        }

        b.swipeLobbys.setOnRefreshListener {
            emit("getLobbys", null)
        }
    }

    override fun onResume() {
        super.onResume()


        Log.d("LobbyList", "onResume")
        b.rvLobbys.layoutManager = LinearLayoutManager(b.root.context, RecyclerView.VERTICAL, false)
        lobbyAdapter = LobbyAdapter(lobbyList) {
            joinGame(it) // callback function for listview click
        }
        b.rvLobbys.adapter = lobbyAdapter
        Log.d("LobbyList", "Created lobby recycler view")

        getSocket().on("getLobbys") {
            Log.d("LobbyList", "Received lobby list from server")
            val lobbys = (it[0] as JSONArray)
            Log.d("LobbyList", "Lobby count: " + lobbys.length().toString())
            lobbyList.clear()

            for (i in 0 until lobbys.length()) {
                val jsonLobby = lobbys.getJSONObject(i)
                lobbyList.add(jsonLobby.toLobby())
            }

            runOnUiThread(Runnable {
                b.swipeLobbys.isRefreshing = false

                if (lobbys.length() == 0) {
                    // no lobbys found -> show info
                    b.cardNoLobbyFound.visibility = View.VISIBLE
                    b.rvLobbys.visibility = View.GONE
                } else {
                    b.cardNoLobbyFound.visibility = View.GONE
                    b.rvLobbys.visibility = View.VISIBLE
                    lobbyAdapter.notifyDataSetChanged()
                }
            })
            Log.d("LobbyList", "End of lobby refresh")
        }

        emit("getLobbys", null)

        if (!getSocket().connected()) {
            Log.d("LobbyList", "no connection")
        }
    }

    private fun joinGame(gameID: String) {
        Log.d("LobbyList", "Lobby clicked: " + gameID)
        val lobbyIntent = Intent(this, LobbyActivity::class.java)
        lobbyIntent.putExtra("gameID", gameID)
        startActivity(lobbyIntent)
    }
}