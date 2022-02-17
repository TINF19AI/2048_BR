package com.dhbw.br2048.api

import android.util.Log
import android.widget.TextView
import com.dhbw.br2048.data.Score
import com.google.gson.JsonArray
import org.json.JSONObject
import io.socket.client.Socket
import org.json.JSONArray


class GameSocket(sessionName: String, username: String, scoreboard: (ArrayList<Score>, Int) -> Unit){
    var socket: Socket
    var position: Int = 0

    fun score(score: Int) {
        socket.emit("score", score)
    }

    fun over(score: Int) {
        socket.emit("over", score)
    }

    fun won(score: Int) {
        socket.emit("won", score)
    }

    init {
        socket = SocketHandler.manager.socket("/game/$sessionName")

        socket.on(Socket.EVENT_CONNECT) {
            Log.d("EVENT_CONNECT", it.toString())
        }

        socket.on(Socket.EVENT_DISCONNECT) {
            Log.d("EVENT_DISCONNECT", it.toString())
        }

        socket.on("score") {
            val scores = (it[0] as JSONArray)
            val list = arrayListOf<Score>()

            for (i in 0 until scores.length()) {
                val score = scores.getJSONObject(i)

                list.add(
                    Score(
                        score.getString("username"),
                        score.getInt("score"),
                        score.getBoolean("alive")
                    )
                )

                if(score.getString("username") == username){
                    position = i + 1
                }
            }

            if (scoreboard != null) {
                scoreboard(list, position)
            }
        }

        socket.open()

    }

    fun close() {
        socket.close()
    }
}