package com.sysaxiom.newsfeed

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                color = Color.White,
                modifier = Modifier.fillMaxSize()
            ) {
                Navigation()
            }
        }
        Handler().postDelayed({
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
        }, 3000)

    }
}

    @Composable
    fun Navigation() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "splash_screen") {
            composable("splash_screen") { SplashScreen(navController = navController) }
            composable("main_screen") {
                MainActivity()
            }
        }
    }

    @Composable
    fun SplashScreen(navController: NavController) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().background(color=Color.White)) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.width(400.dp).height(400.dp)

            )
        }
    }
