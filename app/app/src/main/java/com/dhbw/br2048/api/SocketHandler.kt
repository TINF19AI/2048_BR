package com.dhbw.br2048.api

import android.app.Activity
import android.provider.Settings
import android.util.Log
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import java.net.SocketTimeoutException
import java.net.URI
import java.net.URISyntaxException

object SocketHandler {

    private lateinit var mSocket: Socket
    lateinit var manager: Manager

    @Synchronized
    fun setSocket(username: String, userId: String) {
        if(this::mSocket.isInitialized && mSocket.connected()){
            mSocket.close()
        }

        try {
            val opts = IO.Options()
            opts.forceNew = true
            opts.reconnection = true
            opts.query =
                "CustomId=$userId&CustomUsername=$username"

            manager = Manager(URI.create("https://br2048.welt.sh"), opts)
            mSocket = manager.socket("/")
            mSocket.io().timeout(5 * 1000) // 5 seconds
        } catch (e: URISyntaxException) {
            // @todo
        }

        try {
            mSocket.connect()
        } catch (e: SocketTimeoutException) {
            Log.d("SocketHandler", "socket timeout, could not connect to game server")
        }

        mSocket.on(Socket.EVENT_CONNECT) {
            Log.d("mEVENT_CONNECT", it.toString())
        }

        mSocket.on(Socket.EVENT_DISCONNECT) {
            Log.d("mEVENT_DISCONNECT", it.toString())
        }

        mSocket.on("score") {
            Log.d("mEVENT_SCORE", it.toString())
        }
    }

    @Synchronized
    fun request(topic: String, data: Any?, callback: (Array<Any>) -> Unit?) {
        mSocket.emit(topic, data)

        mSocket.once(topic) {
            callback(it)
        }
    }

    @Synchronized
    fun getSocket(): Socket {
        return mSocket
    }

    @Synchronized
    fun closeConnection() {
        mSocket.disconnect()
    }

    @Synchronized
    fun emit(topic: String, data: Any?) {
        mSocket.emit(topic, data)
    }
}