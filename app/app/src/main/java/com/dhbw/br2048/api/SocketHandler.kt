package com.dhbw.br2048.api

import android.content.Context
import android.provider.Settings
import android.util.Log
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import java.net.URI
import java.net.URISyntaxException

object SocketHandler {

    private lateinit var mSocket: Socket
    lateinit var manager: Manager

    @Synchronized
    fun setSocket(context: Context) {
        try {
            val opts = IO.Options()
            opts.forceNew = true
            opts.reconnection = true
            opts.query = "CustomId=" + Settings.Global.getString(context.contentResolver, "device_name")

            manager = Manager(URI.create("http://f322-2003-c2-2f23-300-215-5dff-fe62-e100.ngrok.io"), opts)
            mSocket = manager.socket("/")
        } catch (e: URISyntaxException) {
            // @todo
        }
        mSocket.connect()

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
    fun getSocket(): Socket {
        return mSocket
    }

    @Synchronized
    fun closeConnection() {
        mSocket.disconnect()
    }

    @Synchronized
    fun emit(topic: String, data: Any ) {
        mSocket.emit(topic, data)
    }
}