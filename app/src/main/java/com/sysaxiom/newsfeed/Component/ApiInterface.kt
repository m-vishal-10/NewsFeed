package com.sysaxiom.newsfeed.Component

import com.sysaxiom.newsfeed.Model.Welcome
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("v2/everything")
    suspend fun getHeadlines(
        @Query("language")
        languageCode: String,
        @Query("q")
        userQuery: String,
        @Query("from")
        from:String,
        @Query("sortBy")
        sortBy: String,
        @Query("apiKey")
        apiKey: String
    ): Response<Welcome>
}