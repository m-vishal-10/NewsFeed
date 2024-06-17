package com.sysaxiom.newsfeed
import com.sysaxiom.newsfeed.component.ApiInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE = "https://newsapi.org/"

    val api: ApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl(BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }
}