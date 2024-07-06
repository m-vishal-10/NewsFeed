@file:Suppress("DEPRECATION")

package com.sysaxiom.newsfeed

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.gson.Gson
import com.sysaxiom.newsfeed.model.Article
import com.sysaxiom.newsfeed.ui.theme.NewsFeedTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow

class MainActivity : ComponentActivity() {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val reqCode: Int = 123
    private lateinit var firebaseAuth: FirebaseAuth
    private var isLoggedIn = mutableStateOf(false)

    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()

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
        mainViewModel.sendRequest()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == reqCode) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                updateUi(account)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUi(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Successfully LoggedIn", Toast.LENGTH_SHORT).show()
                isLoggedIn.value = true
            } else {
                Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Composable
    fun NavigationGraph() {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "splash_screen") {
            composable("splash_screen") {
                SplashScreen(navController)
            }
            composable("google_sign_in") {
                GoogleSignInUi(navController, isLoggedIn)
            }
            composable("main_screen") {
                HomeScreen(navController, mainViewModel.filteredArticles)
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
            //TODO :- navigate to sign in screen
            navController.navigate("google_sign_in") {
                popUpTo("splash_screen") { inclusive = true }
            }
        }
    }

    @Composable
    fun HomeScreen(navController: NavController, newsData: StateFlow<List<Article?>>) {
        val newsDataState by newsData.collectAsState(initial = emptyList())
        //val isLoading = mainViewModel.isLoading.observeAsState()

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
                        mainViewModel.filterArticles(it)
                    },
                    placeholder = { Text(text = "search") },
                    label = { Text("Search News", color = Color.Black) },
                    textStyle = TextStyle(fontWeight = FontWeight.Bold)
                )
            }
            Divider()

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                if (isLoading.value == true) {
//                    //TODO :- progress bar
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .background(Color.Black),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        CircularProgressIndicator(
//                            color = Color.Blue,
//                            modifier = Modifier.scale(scaleX = 1.5f, scaleY = 1.5f)
//                        )
//                    }
//
//                }
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
    fun GoogleSignInUi(navController: NavController, isLoggedIn: MutableState<Boolean>) {
        val user by remember { mutableStateOf(Firebase.auth.currentUser) }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (user == null) {
                Text("Not logged in")
                Button(onClick = {
                    val signInIntent: Intent = mGoogleSignInClient.signInIntent
                    startActivityForResult(signInIntent, reqCode)
                }) {
                    Text("Sign in via Google")
                }
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Logged In as ${user!!.displayName}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            LaunchedEffect(isLoggedIn.value) {
                if (isLoggedIn.value) {
                    navController.navigate("main_screen") {
                        popUpTo("google_sign_in") { inclusive = true }
                    }
                }
            }
        }
    }

}