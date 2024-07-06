package com.sysaxiom.newsfeed

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
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
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import com.sysaxiom.newsfeed.model.Article
import com.sysaxiom.newsfeed.ui.theme.NewsFeedTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

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
        mainViewModel.sendRequest()
    }

    @Composable
    fun NavigationGraph() {
        val navController = rememberNavController()
        FirebaseApp.initializeApp(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        var googleSignInClient = GoogleSignIn.getClient(this, gso)

        var auth = FirebaseAuth.getInstance()

        NavHost(navController = navController, startDestination = "splash_screen") {
            composable("splash_screen") {
                SplashScreen(navController)
            }
            composable("google_sign_in") {
                GoogleSignInUI(googleSignInClient, auth, navController)
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
        val isLoading = mainViewModel.isLoading.observeAsState()

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
                if (isLoading.value == true) {
                    //TODO :- progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.Blue,
                            modifier = Modifier.scale(scaleX = 1.5f, scaleY = 1.5f)
                        )
                    }

                } else {
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
    fun GoogleSignInUI(
        googleSignInClient: GoogleSignInClient,
        auth: FirebaseAuth,
        navController: NavHostController
    ) {
        val context = LocalContext.current
        var user by remember { mutableStateOf<FirebaseUser?>(null) }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d("SignIn", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!, auth) { signedInUser ->
                    user = signedInUser
                    if (signedInUser != null) {
                        Log.w("SignIn", "Google sign in success")
                        navController.navigate("main_screen") {
                            popUpTo("google_sign_in") { inclusive = true }
                        }
                    }
                }
            } catch (e: ApiException) {
                Log.e("SignIn", "Google sign in failed", e)
                Log.e("SignIn", "signInResult:failed code=${e.statusCode}, message=${e.message}")
            }
        }

        LaunchedEffect(Unit) {
            user = auth.currentUser
            if (user != null) {
                navController.navigate("main_screen") {
                    popUpTo("google_sign_in") { inclusive = true }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (user == null) {
                Button(
                    onClick = {
                        val signInIntent = googleSignInClient.signInIntent
                        launcher.launch(signInIntent)
                    }
                ) {
                    Text("Sign in with Google", color = Color.White)
                }
            }
        }
    }


    private fun firebaseAuthWithGoogle(
        idToken: String,
        auth: FirebaseAuth,
        onResult: (FirebaseUser?) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val user = auth.currentUser
                    onResult(user)
                } else {
                    onResult(null)
                }
            }
    }


}