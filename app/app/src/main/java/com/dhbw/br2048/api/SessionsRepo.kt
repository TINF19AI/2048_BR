package com.dhbw.br2048.api

import android.view.View
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SessionsRepo {
    private val sessionsApi: SessionsApi

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("") // to be added
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        sessionsApi = retrofit.create(SessionsApi::class.java)
    }

    // Method for API-Call to get a list of cocktails
    fun getOpenSessionsList(callback: Callback<SessionsApiResult>) {
        val cocktailApiResultCall = sessionsApi.getOpenSessionsListFromApi()
        cocktailApiResultCall.enqueue(callback)
    }
}