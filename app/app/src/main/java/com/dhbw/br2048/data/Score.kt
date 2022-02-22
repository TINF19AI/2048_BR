package com.dhbw.br2048.data

import org.json.JSONObject

data class Score(
    val userId: String,
    val username: String,
    val score: Int,
    val alive: Boolean,
    val position: Int,
)

fun JSONObject.toScore(): Score {
    return Score(
        this.getString("userId"),
        this.getString("username"),
        this.getInt("score"),
        this.getBoolean("alive"),
        this.getInt("position"),
    )
}