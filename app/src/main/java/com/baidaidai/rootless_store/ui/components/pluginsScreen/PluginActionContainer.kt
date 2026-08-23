package com.baidaidai.rootless_store.ui.components.pluginsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom

@Composable
fun PluginActionContainer(
    modifier: Modifier = Modifier,
    pluginManifestRoom: PluginManifestRoom? = null,
    onShareButtonClick: ()-> Unit = {},
    onWebUiButtonClick: ()-> Unit = {},
    onBackButtonClick: ()-> Unit = {},
    onDeleteButtonClick: ()-> Unit = {},
){

    val shareButtonColors = IconButtonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.onPrimary
    )

    val backButtonColors = IconButtonColors(
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary,
        disabledContainerColor = MaterialTheme.colorScheme.secondary,
        disabledContentColor = MaterialTheme.colorScheme.onSecondary
    )

    val webUiButtonColors = IconButtonColors(
        containerColor = MaterialTheme.colorScheme.tertiary,
        contentColor = MaterialTheme.colorScheme.onTertiary,
        disabledContainerColor = MaterialTheme.colorScheme.tertiary,
        disabledContentColor = MaterialTheme.colorScheme.onTertiary
    )

    val deleteButtonColors = IconButtonColors(
        containerColor = MaterialTheme.colorScheme.error,
        contentColor = MaterialTheme.colorScheme.onError,
        disabledContainerColor = MaterialTheme.colorScheme.error,
        disabledContentColor = MaterialTheme.colorScheme.onError
    )

    Surface(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer
                )
        ) {

            IconButton(
                onClick = onShareButtonClick,
                colors = shareButtonColors,
                modifier = Modifier
                    .size(56.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.material_symbols_ios_share),
                    contentDescription = "Share"
                )
            }

            if (!pluginManifestRoom?.webUiEntryPoint.isNullOrEmpty()){
                IconButton(
                    onClick = onWebUiButtonClick,
                    colors = webUiButtonColors,
                    modifier = Modifier
                        .size(56.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.material_symbols_globe_asia),
                        contentDescription = "Share"
                    )
                }
            }

            IconButton(
                onClick = onBackButtonClick,
                colors = backButtonColors,
                modifier = Modifier
                    .size(56.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.material_symbols_undo),
                    contentDescription = "Share"
                )
            }

            IconButton(
                onClick = onDeleteButtonClick,
                colors = deleteButtonColors,
                modifier = Modifier
                    .size(56.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.material_symbols_delete),
                    contentDescription = "Share"
                )
            }

        }
    }

}

@PreviewLightDark
@Composable
private fun _preview_() {
    PluginActionContainer(
        pluginManifestRoom = PluginManifestRoom._testOnly_.copy(webUiEntryPoint = "akjfjdkdjskf"),
        modifier = Modifier
            .size(
                width = 300.dp,
                height = 200.dp
            )
    )
}