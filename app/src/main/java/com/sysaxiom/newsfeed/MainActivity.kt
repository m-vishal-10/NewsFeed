package com.sysaxiom.newsfeed

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sysaxiom.newsfeed.Model.Article
import com.sysaxiom.newsfeed.Model.Welcome
import com.sysaxiom.newsfeed.ui.theme.NewsFeedTheme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MainActivity : ComponentActivity() {
    private var newsData = mutableListOf<Welcome>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsFeedTheme {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()

                    ) {
                        sendRequest()
                        //MyUi(newsData = newsData)
                    }

            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun sendRequest() {
        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                RetrofitInstance.api.getHeadlines("en",
                    "Wipro",
                    "2024-06-11",
                    "relevancy",
                    "b51b0aef13f14d77b766b0f309a5a788")

            }catch (e: IOException){
                Toast.makeText(applicationContext, "io error: ${e.message}", Toast.LENGTH_SHORT).show()
                return@launch
            }
            if(response.isSuccessful && response.body() != null){
                withContext(Dispatchers.Main){
                    val newsData = response.body() ?: return@withContext
                    //MyUi(newsData = newsData)
                }
            }
        }
    }
    @Composable
    fun MyUi(newsData: Welcome) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center) {
            Text(text = "News",Modifier.padding(bottom = 25.dp), fontSize = 26.sp)
            if (newsData.articles.isNotEmpty()) {
                val articles = newsData.articles
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(articles.size) {index ->
                        val article = articles[index]
                        NewsCard(
                            urlToImage = article?.urlToImage?:"",
                            title = article?.title?:"",
                            description = article?.description?:"",
                            publishedAt = article?.publishedAt?:""
                        )
                    }
                }
            }

        }
    }
}


