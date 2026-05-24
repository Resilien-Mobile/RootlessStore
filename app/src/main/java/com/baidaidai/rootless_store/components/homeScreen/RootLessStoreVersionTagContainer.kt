package com.baidaidai.rootless_store.components.homeScreen

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.core.i18n.icuString

@Composable
fun RootLessStoreVersionTagContainer(){

    val versionNumber = stringResource(R.string.app_version)

    val cardColors = CardDefaults.cardColors(
        containerColor = colorScheme.primaryContainer,
        contentColor = colorScheme.onPrimaryContainer,
    )
    Card(
        modifier = Modifier
            .fillMaxWidth(),
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
                    painter = painterResource(R.drawable.terminal_24px),
                    contentDescription = stringResource(R.string.home_screen_version_checker_container_icon_content_description),
                    modifier = Modifier
                        .size(30.dp)
                )
                Spacer(
                    modifier = Modifier
                        .width(20.dp)
                )
                Column{
                    Text(
                        text = stringResource(R.string.home_screen_version_checker_container_headline),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = icuString(
                            R.string.home_screen_version_checker_container_supporting,
                            mapOf("version" to versionNumber)
                        ),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}