package com.baidaidai.rootless_store.ui.components.sourceScreen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BadgeDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.domain.source.model.PluginSource
import com.baidaidai.rootless_store.ui.model.RootlessStoreSourceScreenViewModel

@Composable
fun SourceScreenLeadingDeleteButton(
    sourceScreenViewModel: RootlessStoreSourceScreenViewModel,
    pluginSource: PluginSource
){
    Row {
        IconButton(
            onClick = {
                sourceScreenViewModel.deletePluginSource(pluginSource)
            },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = BadgeDefaults.containerColor,
                contentColor = contentColorFor(BadgeDefaults.containerColor)
            ),
            modifier = Modifier
                .size(20.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.outline_close_small_24),
                contentDescription = stringResource(R.string.sources_screen_list_item_delete_content_description),
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
    }
}
