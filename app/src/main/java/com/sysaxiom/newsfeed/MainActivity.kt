package com.sysaxiom.newsfeed

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.rememberAsyncImagePainter
import com.google.gson.Gson
import com.sysaxiom.newsfeed.model.Article
import com.sysaxiom.newsfeed.ui.theme.NewsFeedTheme
import kotlinx.coroutines.delay
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
                    NavigationGraph()
                }
            }
        }
        viewModel.sendRequest()
    }

    @Composable
    fun NavigationGraph() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "splash_screen") {
            composable("splash_screen") {
                SplashScreen(navController)
            }
            composable("login_screen") {
                LoginScreen(navController)
            }
            composable("main_screen") {
                HomeScreen(navController, viewModel.filteredArticles)
            }
            composable(
                route = "detail_screen/{article}",
                arguments = listOf(
                    navArgument("article") {
                        type = NavType.StringType
                    }
                )
            ) { entry ->
                val articleJson = entry.arguments?.getString("article")
                val article = Gson().fromJson(articleJson, Article::class.java)
                DetailScreen(article, navController)
            }
        }
    }

    @Composable
    fun SplashScreen(navController: NavHostController) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo"
            )
        }

        LaunchedEffect(Unit) {
            delay(1500)
            navController.navigate("login_screen") {
                popUpTo("splash_screen") { inclusive = true }
            }
        }
    }

    @Composable
    fun HomeScreen(navController: NavController, newsData: StateFlow<List<Article?>>) {
        val newsDataState by newsData.collectAsState(initial = emptyList())
        val isLoading by viewModel.isLoading.collectAsState(initial = false)

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
                    text = "News Feed",
                    Modifier.padding(top = 25.dp),
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

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.Blue)
                }
                else {


                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(newsDataState.size) {
                            newsDataState.forEach { article ->
                                NewsCard(
                                    urlToImage = article?.urlToImage ?: "",
                                    title = article?.title ?: "",
                                    description = article?.description ?: "",
                                    publishedAt = article?.publishedAt ?: "",
                                    onClick = {
                                        val articleJson = Gson().toJson(article)
                                        val encodedArticleJson = Uri.encode(articleJson)
                                        navController.navigate(
                                            Screen.DetailScreen.withArgs(
                                                encodedArticleJson
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun DetailScreen(article: Article?, navController: NavController) {

        val context = LocalContext.current

        val url = article?.url.toString()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
            ) {
                IconButton(onClick = {

                    navController.popBackStack()
                }) {
                    Icon(
                        painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        modifier = Modifier.background(Color.White)
                    )
                }

                Text(text = "News Feed", fontSize = 20.sp, modifier = Modifier.padding(7.dp))
                Spacer(modifier = Modifier.weight(1.0f))
                IconButton(onClick = {
                    val intent = Intent(Intent.ACTION_SEND)
                        .putExtra(Intent.EXTRA_TEXT, url)
                        .setType("text/plain")
                    context.startActivity(Intent.createChooser(intent, "Share Using"))
                }) {
                    Icon(
                        painterResource(id = R.drawable.ic_share),
                        contentDescription = "Share",
                        modifier = Modifier
                            .background(Color.White)
                            .size(24.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                article?.let {
                    Spacer(modifier = Modifier.size(50.dp))
                    Image(
                        painter = rememberAsyncImagePainter(it.urlToImage),
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .padding(8.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = it.publishedAt ?: "No Published Date",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 12.sp
                    )
                    Text(
                        text = it.title ?: "No Title",
                        modifier = Modifier.padding(30.dp),
                        fontSize = 30.sp,
                        lineHeight = 28.sp
                    )
                    Text(
                        text = it.description ?: "No Description",
                        modifier = Modifier
                            .padding(16.dp),
                        fontSize = 15.sp
                    )

                }
                Button(
                    onClick = {
                        val uri = Uri.parse(url)
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = "Open Source")
                }
            }
        }
    }


    @Composable
    fun LoginScreen(navController: NavController) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    navController.navigate("main_screen"){
                        popUpTo("login_screen"){
                            inclusive = true
                        }
                    }
                },
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "",
                    modifier = Modifier.size(24.dp)
                )
                Text(text = "Sign in with Google", modifier = Modifier.padding(6.dp))
            }

        }

    }

}