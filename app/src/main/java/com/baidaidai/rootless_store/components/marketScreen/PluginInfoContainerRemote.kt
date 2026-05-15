package com.baidaidai.rootless_store.components.marketScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.domain.plugin.manifest.EnvironmentManifest
import com.baidaidai.rootless_store.domain.plugin.manifest.EnvironmentManifestRemote
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRemote
import com.baidaidai.rootless_store.domain.plugin.manifest.RootlessStoreManifestCollection
import com.baidaidai.rootless_store.domain.plugin.model.PluginType

@Composable
fun PluginInfoContainerRemote(
    manifest: RootlessStoreManifestCollection,
    modifier: Modifier = Modifier,
    onClick: ()-> Unit
){
    when(manifest){
        is PluginManifest -> {
            val pluginManifest = manifest as PluginManifestRemote
            Card(
                modifier = modifier
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ){
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Icon(
                            painter = painterResource(R.drawable.outline_extension_24),
                            contentDescription = "Plugin Icon",
                            modifier = Modifier
                                .size(24.dp)
                        )
                        Spacer(
                            modifier = Modifier
                                .width(8.dp)
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ){
                            Text(
                                text = pluginManifest.pluginRenderingName,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        Button(
                            onClick = onClick,
                            modifier = Modifier
                                .height(40.dp)
                        ) {
                            Text("Install")
                        }
                    }

                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 30.dp),
                    ){
                        PluginTagTonalAssistChip(PluginType.Client)
                        Spacer(modifier = Modifier.width(8.dp))
                        PluginTagTonalAssistChip(pluginManifest.requiredEnvironment)
                        Spacer(modifier = Modifier.width(8.dp))
                        PluginTagTonalAssistChip(pluginManifest.pluginRunModel)
                    }

                    HorizontalDivider()
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PluginInfoRow(label = "Author", value = pluginManifest.author)
                        PluginInfoRow(
                            label = "Description",
                            value = pluginManifest.pluginDescription
                        )
                    }
                }
            }
        }
        is EnvironmentManifest -> {
            val environmentManifest = manifest as EnvironmentManifestRemote
            Card(
                modifier = modifier
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ){
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Icon(
                            painter = painterResource(R.drawable.outline_extension_24),
                            contentDescription = "Plugin Icon",
                            modifier = Modifier
                                .size(24.dp)
                        )
                        Spacer(
                            modifier = Modifier
                                .width(8.dp)
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ){
                            Text(
                                text = environmentManifest.environmentRenderingName,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        Button(
                            onClick = onClick,
                            modifier = Modifier
                                .height(40.dp)
                        ) {
                            Text("Install")
                        }
                    }

                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 30.dp),
                    ){
                        PluginTagTonalAssistChip(PluginType.Environment)
                        Spacer(modifier = Modifier.width(8.dp))
                        PluginTagTonalAssistChip(environmentManifest.requiredEnvironment)
                    }

                    HorizontalDivider()
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PluginInfoRow(label = "Author", value = environmentManifest.author)
                        PluginInfoRow(
                            label = "Description",
                            value = environmentManifest.environmentDescription
                        )
                    }
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
            style = MaterialTheme.typography.bodyMedium,
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
    PluginInfoContainerRemote(manifest = PluginManifestRemote._testOnly_){}
}
