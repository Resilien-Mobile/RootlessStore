package com.baidaidai.rootless_store.ui.components.codeBrickScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CodeBrickTileList(
    modifier: Modifier = Modifier,
    tileBinderIndex: Int?,
    onValueChange:(newTileBinderIndex: Int?)-> Unit
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

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
    ) {

        var tileListContentCanSee by remember { mutableStateOf(false) }

        ListItem(
            onClick = { tileListContentCanSee = !tileListContentCanSee },
            trailingContent = {
                Icon(
                    painter = painterResource(R.drawable.material_symbols_keyboard_arrow_down_icon),
                    contentDescription = "Expand More"
                )
            },
            colors = unfocusedListItemStyle
        ) {

            Text(
                text = "Tile Binder (New)",
                style = MaterialTheme.typography.titleMedium
            )

        }
        if (tileListContentCanSee) {

            Spacer(modifier = Modifier.height(2.dp))

            val tileBinderText = tileBinderIndex?.toString().orEmpty()

            BasicTextField(
                value = tileBinderText,
                singleLine = true,
                onValueChange = { newValue ->
                    if (newValue.isEmpty()) {
                        onValueChange(null)
                        return@BasicTextField
                    }

                    val number = newValue.toIntOrNull()
                    if (number != null && number in 0..4) {
                        onValueChange(number)
                    }
                },
                decorationBox = { innerTextField ->
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        if (tileBinderText.isEmpty()) {
                            Text(
                                text = "Input tile binder： 0-4",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        innerTextField()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
            )

        }
    }
}

@PreviewLightDark
@Composable
private fun _CodeBrickTileListPreview_() {
    CodeBrickTileList(tileBinderIndex = null, onValueChange = {})
}
