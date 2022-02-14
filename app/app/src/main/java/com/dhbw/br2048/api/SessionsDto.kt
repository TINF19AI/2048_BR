package com.dhbw.br2048.api

import com.google.gson.annotations.SerializedName

data class SessionsDto (
    @SerializedName("idSession")
    val idSession : String,

    @SerializedName("idSession")
    val nameSession : String,

    @SerializedName("idSession")
    val playerCountSession : String,
    )
