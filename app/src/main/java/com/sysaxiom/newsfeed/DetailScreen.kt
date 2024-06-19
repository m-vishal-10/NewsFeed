package com.sysaxiom.newsfeed


import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.sysaxiom.newsfeed.model.Article


@Composable

fun DetailScreen(article: Article?) {
    val context = LocalContext.current
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
                context.startActivity(Intent(context, MainActivity::class.java))
            }) {
                Icon(
                    painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    modifier = Modifier.background(Color.White)
                )
            }

            Text(text = "Newsly", fontSize = 20.sp, modifier = Modifier.padding(7.dp))
            Spacer(modifier = Modifier.weight(1.0f))
            IconButton(onClick = { }) {
                Icon(
                    painterResource(id = R.drawable.ic_share),
                    contentDescription = "Share",
                    modifier = Modifier.background(Color.White)
                )
            }
        }
        Divider()
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            article?.let {
                Image(
                    painter = rememberAsyncImagePainter(it.urlToImage),
                    contentDescription = null,
                    modifier = Modifier
                        .size(130.dp)
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
                    fontSize = 30.sp
                )
                Text(
                    text = it.description ?: "No Description",
                    modifier = Modifier
                        .padding(16.dp),
                    fontSize = 15.sp
                )

            }

            Button(
                onClick = { },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Open Source")
            }
        }
    }
}


