package com.dhbw.br2048.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.br2048.R
import com.dhbw.br2048.api.SocketHandler.emit
import com.dhbw.br2048.api.SocketHandler.getSocket
import com.dhbw.br2048.data.Lobby
import com.dhbw.br2048.data.toLobby
import com.dhbw.br2048.databinding.ActivityLobbyListBinding
import org.json.JSONArray

class LobbyListActivity : AppCompatActivity() {

    private lateinit var b: ActivityLobbyListBinding

    // For Lobby RecyclerView
    private val lobbyList: MutableList<Lobby> = mutableListOf()
    private lateinit var lobbyAdapter: LobbyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        val sp = getSharedPreferences("theme", MODE_PRIVATE)
        setTheme(sp.getInt("currentTheme", R.style.Theme_Original))
        super.onCreate(savedInstanceState)

        b = ActivityLobbyListBinding.inflate(layoutInflater)
        setContentView(b.root)
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
                lobbyAdapter.notifyDataSetChanged()
            })
            Log.d("LobbyList", "End of lobby refresh")
        }

        emit("getLobbys", null)
    }

    private fun joinGame(gameID: String) {
        Log.d("LobbyList", "Lobby clicked: " + gameID)
        val lobbyIntent = Intent(this, LobbyActivity::class.java)
        lobbyIntent.putExtra("gameID", gameID)
        startActivity(lobbyIntent)
    }
}