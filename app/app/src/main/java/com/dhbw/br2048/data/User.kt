package com.dhbw.br2048.data

import org.json.JSONObject

data class User(
    val username: String,
)

fun JSONObject.toUser(): User {
    return User(
        this.getString("username"),
    )
}