package com.sysaxiom.newsfeed.model

data class Welcome(
    val articles:List<Article?>,
    val status: String?,
    val totalResults: Int?
)