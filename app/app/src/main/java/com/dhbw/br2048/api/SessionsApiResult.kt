package com.dhbw.br2048.api

import com.google.gson.annotations.SerializedName

data class SessionsApiResult (
    @SerializedName("sessions")
    val sessionList: List<SessionsDto>
    )