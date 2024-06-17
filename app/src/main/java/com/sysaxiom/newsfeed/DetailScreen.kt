package com.sysaxiom.newsfeed


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DetailScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { }) {
                Icon(
                    painterResource(id = R.drawable.ic_back),
                    contentDescription = "Localized description",
                    modifier = Modifier.background(Color.White)
                )
            }

            Text(text = "Newsly", fontSize = 20.sp, modifier = Modifier.padding(7.dp))
            Spacer(modifier = Modifier.weight(1.0f))
            IconButton(onClick = { }) {
                Icon(
                    painterResource(id = R.drawable.ic_share),
                    contentDescription = "",
                    modifier = Modifier.background(Color.White)
                )
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Image(painter = , contentDescription = )
            Text(
                text = "title",
                modifier = Modifier.padding(top = 50.dp, bottom = 16.dp), fontSize = 30.sp
            )

            Text(
                text = "Description",
                modifier = Modifier.padding(16.dp), fontSize = 30.sp
            )
            Button(
                onClick = { },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Open Source")
            }
        }

    }

}


@Preview
@Composable
fun DetailScreenPreview() {
    DetailScreen()
}

