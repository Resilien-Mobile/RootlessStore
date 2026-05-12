package com.baidaidai.rootless_store.components.sourcesScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.domain.source.model.PluginSourceInfo
import com.baidaidai.rootless_store.ui.model.RootLessStoreSourceScreenViewModel
import com.baidaidai.rootless_store.ui.theme.colorscheme.SourceListItemColor

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SourceScreenListItem(
    sourceScreenLeadingDeleteButtonStatus: Boolean,
    pluginSourceInfo: PluginSourceInfo,
    sourceScreenViewModel: RootLessStoreSourceScreenViewModel,
    onListItemClick:(pluginSourceUri: String)-> Unit
){
    ListItem(
        onClick = {
            onListItemClick(pluginSourceInfo.sourceRemoteEndpoint)
        },
        supportingContent = {
            Text(pluginSourceInfo.sourceRemoteEndpoint)
        },
        trailingContent = {
            Icon(
                painter = painterResource(R.drawable.outline_arrow_forward_ios_24),
                contentDescription = stringResource(R.string.sources_screen_list_item_go_to_content_description)
            )
        },
        colors = SourceListItemColor(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        leadingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (sourceScreenLeadingDeleteButtonStatus){
                    SourceScreenLeadingDeleteButton(sourceScreenViewModel,pluginSourceInfo)
                }
                // Should Change Intro Compatible Source
                Image(
                    painter = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = stringResource(R.string.sources_screen_list_item_icon_content_description),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
            }
        }
    ) {
        Text(pluginSourceInfo.sourceName)
    }
}