package com.sysaxiom.newsfeed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sysaxiom.newsfeed.model.Article
import com.sysaxiom.newsfeed.ui.theme.NewsFeedTheme
import kotlinx.coroutines.flow.StateFlow

class MainActivity : ComponentActivity() {
    private val viewModel = MainViewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsFeedTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()

                ) {
                    HomeScreen(viewModel.filteredArticles)
                }

            }
        }
        viewModel.sendRequest()
    }

    @Composable
    fun HomeScreen(newsData: StateFlow<List<Article?>>) {

        val newsDataState by newsData.collectAsState(initial = emptyList())

        val searchText = remember {
            mutableStateOf("")
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Column {
                Text(
                    text = "Hot News",
                    Modifier.padding(bottom = 25.dp, top = 25.dp),
                    fontSize = 26.sp,
                    fontStyle = FontStyle.Italic
                )
                OutlinedTextField(
                    value = searchText.value,
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    onValueChange = {
                        searchText.value = it
                        viewModel.filterArticles(it)
                    },
                    placeholder = { Text(text = "search") },
                    label = { Text("Search News", color = Color.Black) },
                    textStyle = TextStyle(fontWeight = FontWeight.Bold)
                )

            }
            Divider()
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(newsDataState.size) {
                    newsDataState.forEach { article ->
                        NewsCard(
                            urlToImage = article?.urlToImage ?: "",
                            title = article?.title ?: "",
                            description = article?.description ?: "",
                            publishedAt = article?.publishedAt ?: ""
                        )
                    }
                }
            }
        }
    }


}
