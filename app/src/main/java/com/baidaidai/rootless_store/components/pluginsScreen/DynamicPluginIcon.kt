package com.baidaidai.rootless_store.components.pluginsScreen

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import com.baidaidai.rootless_store.R

@Composable
fun DynamicPluginIcon(
    modifier: Modifier = Modifier,
    iconUri: Uri?,
    contentDescription: String
){
    val defaultPluginIcon: @Composable () -> Unit = {
        Icon(
            painter = painterResource(R.drawable.outline_extension_24),
            contentDescription = contentDescription,
            modifier = modifier.size(24.dp)
        )
    }

    if (iconUri == null){
        defaultPluginIcon()
    }else{
        SubcomposeAsyncImage(
            model = iconUri,
            contentDescription = contentDescription,
            modifier = modifier
        ) {

            val state by painter.state.collectAsState()

            when(state){
                is AsyncImagePainter.State.Success -> {
                    SubcomposeAsyncImageContent(
                        modifier = modifier
                            .padding(6.dp)
                            .size(40.dp)
                    )
                }
                is AsyncImagePainter.State.Loading -> {}
                else -> {
                    defaultPluginIcon()
                }
            }
        }
    }
}