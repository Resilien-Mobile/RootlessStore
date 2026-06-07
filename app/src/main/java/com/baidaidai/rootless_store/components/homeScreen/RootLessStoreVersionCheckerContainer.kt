package com.baidaidai.rootless_store.components.homeScreen

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.baidaidai.rootless_store.R

@Composable
fun RootLessStoreVersionCheckerContainer(
    latestVersionNumber: String
){

    val context = LocalContext.current
    val cardColors = CardDefaults.cardColors(
        containerColor = colorScheme.secondaryContainer,
        contentColor = colorScheme.onSecondaryContainer,
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = true,
                onClick = {
                    val url = "https://github.com/Resilien-Mobile/RootlessStore/releases/latest"
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    context.startActivity(intent)
                }
            ),
        elevation = CardDefaults.cardElevation(),
        colors = cardColors
    ){
        Column(
            modifier = Modifier
                .padding(horizontal = 30.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    painter = painterResource(R.drawable.material_symbols_bottom),
                    contentDescription = stringResource(R.string.home_screen_version_checker_container_new_version_icon_content_description),
                    modifier = Modifier
                        .size(30.dp)
                )
                Spacer(
                    modifier = Modifier
                        .width(20.dp)
                )
                Column{
                    Text(
                        text = stringResource(R.string.home_screen_version_checker_container_new_version_headline),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = latestVersionNumber,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}