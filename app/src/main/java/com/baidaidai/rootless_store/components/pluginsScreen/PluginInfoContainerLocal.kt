package com.baidaidai.rootless_store.components.pluginsScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.core.i18n.icuString
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom

@Composable
fun PluginInfoContainerLocal(
    badgeShowState: Boolean = true,
    pluginManifest: PluginManifestRoom,
    onSwitchClick: ()-> Unit,
    onBadgeClick:()-> Unit,
    onCardClick:()-> Unit,
){
    BadgedBox(
        badge = {
            if (badgeShowState){
                Badge(
                    modifier = Modifier
                        .size(16.dp)
                ){
                    IconButton(
                        onClick = { onBadgeClick() }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_close_24),
                            contentDescription = stringResource(R.string.plugin_screen_info_container_local_delete_button_content_description),
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .clickable{ onCardClick() }
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
                    Icon(
                        painter = painterResource(R.drawable.outline_extension_24),
                        contentDescription = stringResource(R.string.plugin_screen_info_container_local_icon_content_description),
                        modifier = Modifier
                            .size(24.dp)
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
                            text = pluginManifest.pluginRenderingName,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = icuString(
                                R.string.plugin_screen_info_container_local_version,
                                mapOf("version" to pluginManifest.installedVersion)
                            ),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Switch(
                        checked = pluginManifest.enabled,
                        onCheckedChange = { onSwitchClick() }
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
                    PluginInfoRow(
                        label = stringResource(R.string.plugin_screen_info_container_local_author_label),
                        value = pluginManifest.author
                    )
                    PluginInfoRow(
                        label = stringResource(R.string.plugin_screen_info_container_local_source_label),
                        value = pluginManifest.source.toString()
                    )
                    PluginInfoRow(
                        label = stringResource(R.string.plugin_screen_info_container_local_state_label),
                        value = pluginManifest.state.toString()
                    )
                    PluginInfoRow(
                        label = stringResource(R.string.plugin_screen_info_container_local_required_label),
                        value = pluginManifest.requiredEnvironment.toString()
                    )
                }
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
private fun PluginInfoRow(
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
private fun _PluginInfosContainerPreview_(){
    PluginInfoContainerLocal(
        pluginManifest = PluginManifestRoom._testOnly_,
        onSwitchClick = {},
        onBadgeClick = {}
    ){}
}
