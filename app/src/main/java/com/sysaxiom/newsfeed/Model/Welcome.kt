package com.sysaxiom.newsfeed.Model

data class Welcome(
    val articles:List<Article?>,
    val status: String?,
    val totalResults: Int?
)