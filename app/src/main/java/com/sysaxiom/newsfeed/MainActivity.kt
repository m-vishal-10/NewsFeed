package com.sysaxiom.newsfeed

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
                    HomeScreen(newsData = newsData)
                }

            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun sendRequest() {

        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                RetrofitInstance.api.getHeadlines(
                    "en",
                    "Wipro",
                    "2024-06-11",
                    "relevancy",
                    "b51b0aef13f14d77b766b0f309a5a788"
                )

            } catch (e: IOException) {
                Toast.makeText(applicationContext, "io error: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                return@launch
            }
            print(response)
            if (response.isSuccessful && response.body() != null) {
                withContext(Dispatchers.Main) {
                    val newsData = response.body() ?: return@withContext
                }
            }
        }
    }

    @Composable
    fun HomeScreen(newsData: MutableList<Welcome>) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "News", Modifier.padding(bottom = 25.dp, top = 25.dp), fontSize = 26.sp)

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {

                items(5) {
//                    index -> val article = articles[index]
                    NewsCard(
                        urlToImage = "https://www.livemint.com/lm-img/img/2024/06/13/1600x900/VISA-SF-OFFICE-14_1717619013261_1718260431430.jpg",//article?.urlToImage ?: "",
                        title = "Bodies of 46 victims who died in Kuwait fire reach Kerala",//article?.title ?: ,
                        description = "23 of the 46 victims are from Kerala, seven from Tamil Nadu, four from Uttar Pradesh, three from Andhra Pradesh and two each from Bihar and Odisha. The victims also include one person each from Jharkhand, Karnataka, Maharashtra, Punjab and West Bengal.",//article?.description ?: "",
                        publishedAt = "2024-06-13T06:48:08Z"//article?.publishedAt ?: ""
                    )
                }
            }

        }
    }
}


