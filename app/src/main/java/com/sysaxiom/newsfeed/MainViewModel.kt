package com.sysaxiom.newsfeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sysaxiom.newsfeed.model.Article
import com.sysaxiom.newsfeed.model.Welcome
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class MainViewModel : ViewModel() {

    private var newsData: Welcome? = null
    private val _filteredArticles = MutableStateFlow<List<Article?>>(emptyList())
    val filteredArticles: StateFlow<List<Article?>> = _filteredArticles.asStateFlow()
    fun sendRequest() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getHeadlines(
                    "en",
                    "hi",
                    "2024-06-11",
                    "relevancy",
                    "b51b0aef13f14d77b766b0f309a5a788"
                )
                if (response.isSuccessful) {
                    response.body()?.let { news ->
                        newsData = news
                        _filteredArticles.value = newsData?.articles ?: emptyList()
                    }
                }
            } catch (e: IOException) {
                println("IO Exception: ${e.message}")
            }
        }
    }

    fun filterArticles(query: String) {
        viewModelScope.launch {
            viewModelScope.launch {
                newsData?.articles?.let { articles ->
                    _filteredArticles.value = articles.filter { article ->
                        article?.title?.contains(query, ignoreCase = true) == true
                    }
                }
            }
        }
    }
}


