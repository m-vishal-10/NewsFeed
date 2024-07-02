package com.sysaxiom.newsfeed

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@Composable
fun NewsCard(
    urlToImage: String,
    title: String,
    description: String,
    publishedAt: String,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .padding(10.dp)
            .height(200.dp)
            .wrapContentHeight()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()) {
            Column (
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .align(Alignment.CenterVertically)
            ){
                Image(
                    painter = rememberAsyncImagePainter(urlToImage),
                    contentDescription = null,
                    modifier = Modifier
                        .size(130.dp)
                        .aspectRatio(1f)
                        .padding(top = 25.dp, start = 8.dp),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = publishedAt,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = 8.sp
                )
            }
            Column (modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
                .padding(5.dp)){
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(5.dp),
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
