package com.dhbw.br2048.data

import org.json.JSONObject

data class Lobby(
    val id: String,
    var owner: String,
    var currentUsers: Int,
    var maxUsers: Int,
    var duration: Int
)

fun JSONObject.toLobby(): Lobby {
    return Lobby(
        this.getString("id"),
        this.getString("owner"),
        this.getInt("currentUsers"),
        this.getInt("maxUsers"),
        this.getInt("duration")
    )
}