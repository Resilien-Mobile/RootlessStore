package com.baidaidai.rootless_store.components.homeScreen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.baidaidai.rootless_store.R
import androidx.core.net.toUri

@Composable
fun HowToDevelopRootlessStorePlugin(){
    val context = LocalContext.current
    val cardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = true,
                onClick = {
                    val url = "https://resilien-mobile.github.io/RootlessStore_WiKi/plugin-development/"
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    context.startActivity(intent)
                }
            )
        ,
        elevation = CardDefaults.cardElevation(),
        colors = cardColors
    ){
        Column(
            modifier = Modifier
                .padding(30.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    painter = painterResource(R.drawable.outline_construction_24),
                    contentDescription = "Develop Icon",
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(color = MaterialTheme.colorScheme.secondaryFixed)
                        .size(40.dp)
                        .padding(7.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryFixed
                )
                Spacer(
                    modifier = Modifier
                        .width(20.dp)
                )
                Column{
                    Text(
                        text = "Learn Rootless Store",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Learn how to develop RootlessStore plugins",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}