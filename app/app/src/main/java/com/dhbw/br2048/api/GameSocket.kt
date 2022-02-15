package com.dhbw.br2048.api

import android.util.Log
import android.widget.TextView
import org.json.JSONObject
import io.socket.client.Socket


class GameSocket(sessionName: String, scoreboard: ((String) -> Unit)? = null){
    var socket: Socket

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
            val gameScore = it[0] as JSONObject
            var content = "Scoreboard: \n"
            for (key in gameScore.keys()) {
                Log.d("EVENT_SCORE$key", (gameScore[key as String] as JSONObject).get("score").toString())
                content += key + " " + (gameScore[key as String] as JSONObject).get("score").toString() + "\n"
            }

            if (scoreboard != null) {
                scoreboard(content)
            }
        }

        socket.open()

    }

    fun close() {
        socket.close()
    }
}