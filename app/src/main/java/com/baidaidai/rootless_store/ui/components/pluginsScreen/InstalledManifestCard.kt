package com.baidaidai.rootless_store.ui.components.pluginsScreen

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.core.i18n.icuString
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestRoom
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import com.baidaidai.rootless_store.domain.plugin.model.PluginRunModel

@Composable
fun InstalledManifestCard(
    pluginManifestRoom: PluginManifestRoom,
    onSizeChanged: (intSize: IntSize)-> Unit = {},
    onEnabledChange: (isEnabled: Boolean)-> Unit,
    onExecuteClick: ()-> Unit,
    onClick: ()-> Unit,
    onLongClick: ()-> Unit = {}
){

    var isExecutionIndicatorActive by remember { mutableStateOf(false) }
    val iconButtonColors = IconButtonColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
        disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
    )


    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.large)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .onSizeChanged(onSizeChanged)
        ,
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraSmall)
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                ,
                verticalAlignment = Alignment.CenterVertically
            ){
                DynamicPluginIcon(
                    iconUri = pluginManifestRoom.iconUri?.toUri(),
                    contentDescription = "Plugin Icon",
                    modifier = Modifier
                        .clip(CircleShape)
                )
                Spacer(
                    modifier = Modifier
                        .width(12.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                ){
                    Text(
                        text = pluginManifestRoom.pluginRenderingName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = icuString(
                            R.string.plugin_screen_info_container_local_version,
                            mapOf("version" to pluginManifestRoom.installedVersion)
                        ),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                if (pluginManifestRoom.pluginRunModel == PluginRunModel.Daemon){
                    Switch(
                        checked = pluginManifestRoom.isEnabled,
                        onCheckedChange = onEnabledChange
                    )
                }else{
                    IconButton(
                        onClick = {
                            onExecuteClick()
                            isExecutionIndicatorActive = !isExecutionIndicatorActive
                        },
                        colors = iconButtonColors,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .size(56.dp)
                    ) {
                        if (isExecutionIndicatorActive){
                            Icon(
                                painterResource(R.drawable.material_symbols_check),
                                contentDescription = "started"
                            )
                        }else{
                            Icon(
                                painter = painterResource(R.drawable.material_symbols_play_arrow),
                                contentDescription = "start"
                            )
                        }
                    }
                }
            }
        }
        Spacer(
            modifier = Modifier
                .height(2.dp)
        )
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraSmall)
        ){
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .padding(24.dp)
            ) {
                ManifestDetailRow(
                    label = stringResource(R.string.plugin_screen_info_container_local_author_label),
                    value = pluginManifestRoom.author
                )
                ManifestDetailRow(
                    label = stringResource(R.string.plugin_screen_info_container_local_source_label),
                    value = pluginManifestRoom.origin.toString()
                )
                ManifestDetailRow(
                    label = stringResource(R.string.plugin_screen_info_container_local_state_label),
                    value = pluginManifestRoom.state.toString()
                )
                ManifestDetailRow(
                    label = stringResource(R.string.plugin_screen_info_container_local_required_label),
                    value = pluginManifestRoom.requiredEnvironment.toString()
                )
            }
        }
    }
}

@Composable
fun InstalledManifestCard(
    environmentManifest: EnvironmentManifestRoom,
    onSizeChanged: (intSize: IntSize)-> Unit = {},
    onEnabledChange: (isEnabled: Boolean)-> Unit,
    onClick:()-> Unit = {},
    onLongClick: ()-> Unit = {}
){
    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.large)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .onSizeChanged(onSizeChanged)
        ,
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraSmall)
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                ,
                verticalAlignment = Alignment.CenterVertically
            ){
                DynamicPluginIcon(
                    iconUri = environmentManifest.iconUri?.toUri(),
                    contentDescription = "Plugin Icon",
                    modifier = Modifier
                        .clip(CircleShape)
                )
                Spacer(
                    modifier = Modifier
                        .width(12.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                ){
                    Text(
                        text = environmentManifest.environmentRenderingName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = icuString(
                            R.string.plugin_screen_info_container_local_version,
                            mapOf("version" to environmentManifest.installedVersion)
                        ),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Switch(
                    checked = environmentManifest.isEnabled,
                    onCheckedChange = onEnabledChange
                )
            }
        }
        Spacer(
            modifier = Modifier
                .height(2.dp)
        )
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraSmall)
        ){
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .padding(24.dp)
            ) {
                ManifestDetailRow(
                    label = stringResource(R.string.plugin_screen_info_container_local_author_label),
                    value = environmentManifest.author
                )
                ManifestDetailRow(
                    label = stringResource(R.string.plugin_screen_info_container_local_source_label),
                    value = environmentManifest.origin.toString()
                )
                ManifestDetailRow(
                    label = stringResource(R.string.plugin_screen_info_container_local_state_label),
                    value = environmentManifest.state.toString()
                )
                ManifestDetailRow(
                    label = stringResource(R.string.plugin_screen_info_container_local_required_label),
                    value = environmentManifest.requiredEnvironment.toString()
                )
            }
        }
    }
}

/**
 * Generated by Codex
 *
 * If Problems, Needs Review !
 */
@Composable
private fun ManifestDetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier
                .weight(0.35f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .weight(0.65f)
        )
    }
}

@Composable
@PreviewLightDark
private fun InstalledManifestCardPreview(){
    InstalledManifestCard(
        pluginManifestRoom = PluginManifestRoom._testOnly_,
        onEnabledChange = {},
        onExecuteClick = {},
        onClick = {},
        onLongClick = {}
    )
}
