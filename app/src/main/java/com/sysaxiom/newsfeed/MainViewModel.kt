package com.sysaxiom.newsfeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sysaxiom.newsfeed.model.Welcome
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class MainViewModel : ViewModel() {
    private val _newsData = MutableStateFlow<List<Welcome>>(emptyList())
    val newsData: StateFlow<List<Welcome>> = _newsData.asStateFlow()

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
                if (response.isSuccessful && response.body() != null) {
                    _newsData.value = listOf(response.body()!!)
                }
            } catch (e: IOException) {
                println("IO Exception: ${e.message}")
            }
        }
    }
}