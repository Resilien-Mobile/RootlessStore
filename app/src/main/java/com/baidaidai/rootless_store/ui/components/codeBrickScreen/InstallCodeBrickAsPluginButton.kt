package com.baidaidai.rootless_store.ui.components.codeBrickScreen

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun InstallCodeBrickAsPluginButton(
    modifier: Modifier = Modifier,
    onClick:()-> Unit = {}
){

    val unfocusedListItemStyle = ListItemColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        leadingContentColor = MaterialTheme.colorScheme.onSurface,
        trailingContentColor = MaterialTheme.colorScheme.onSurface,
        overlineContentColor = MaterialTheme.colorScheme.onSurface,
        supportingContentColor = MaterialTheme.colorScheme.onSurface,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        disabledContentColor = MaterialTheme.colorScheme.onSurface,
        disabledLeadingContentColor = MaterialTheme.colorScheme.onSurface,
        disabledTrailingContentColor = MaterialTheme.colorScheme.onSurface,
        disabledOverlineContentColor = MaterialTheme.colorScheme.onSurface,
        disabledSupportingContentColor = MaterialTheme.colorScheme.onSurface,
        selectedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        selectedContentColor = MaterialTheme.colorScheme.onSurface,
        selectedLeadingContentColor = MaterialTheme.colorScheme.onSurface,
        selectedTrailingContentColor = MaterialTheme.colorScheme.onSurface,
        selectedOverlineContentColor = MaterialTheme.colorScheme.onSurface,
        selectedSupportingContentColor = MaterialTheme.colorScheme.onSurface,
        draggedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        draggedContentColor = MaterialTheme.colorScheme.onSurface,
        draggedLeadingContentColor = MaterialTheme.colorScheme.onSurface,
        draggedTrailingContentColor = MaterialTheme.colorScheme.onSurface,
        draggedOverlineContentColor = MaterialTheme.colorScheme.onSurface,
        draggedSupportingContentColor = MaterialTheme.colorScheme.onSurface,
    )

    ListItem(
        onClick = onClick,
        trailingContent = {
            Icon(
                painter = painterResource(R.drawable.material_symbols_play_arrow),
                contentDescription = stringResource(R.string.code_brick_screen_to_plugin_button_content_description)
            )
        },
        content = {
            Text(
                text = stringResource(R.string.code_brick_screen_to_plugin_button_label),
                style = MaterialTheme.typography.titleMedium
            )
        },
        colors = unfocusedListItemStyle,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
    )
}

@PreviewLightDark
@Composable
private fun _preview_() {
    InstallCodeBrickAsPluginButton()
}
