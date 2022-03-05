package com.dhbw.br2048.api

import android.util.Log
import com.dhbw.br2048.data.Constants
import com.dhbw.br2048.data.Score
import com.dhbw.br2048.data.toScore
import io.socket.client.Socket
import org.json.JSONArray


class GameSocket(sessionName: String, userId: String, scoreboard: (ArrayList<Score>, Score) -> Unit){
    var socket: Socket
    lateinit var currentScore: Score

    fun score(score: Int) {
        socket.emit(Constants.SOCK_SCORE, score)
    }

    fun over(score: Int) {
        socket.emit(Constants.SOCK_OVER, score)
    }

    fun won(score: Int) {
        socket.emit(Constants.SOCK_WON, score)
    }

    fun startGame() {
        socket.emit(Constants.SOCK_START, null)
    }

    init {
        socket = SocketHandler.manager.socket("/game/$sessionName")

        socket.on(Socket.EVENT_CONNECT) {
            Log.d("EVENT_CONNECT", it.toString())
        }

        socket.on(Socket.EVENT_DISCONNECT) {
            Log.d("EVENT_DISCONNECT", it.toString())
        }

        socket.on(Constants.SOCK_SCORE) {
            val scores = (it[0] as JSONArray)
            val list = arrayListOf<Score>()

            for (i in 0 until scores.length()) {
                val score = scores.getJSONObject(i).toScore()

                list.add(score)

                if(score.userId == userId){
                    currentScore = score
                }
            }

            scoreboard(list, currentScore)

        }

        socket.open()

    }

    fun close() {
        socket.close()
    }

    fun lobbyDetails() {
        socket.emit(Constants.SOCK_LOBBYDETAILS, null)
    }

    @Synchronized
    fun isConnected(): Boolean {
        return socket.connected()
    }
}