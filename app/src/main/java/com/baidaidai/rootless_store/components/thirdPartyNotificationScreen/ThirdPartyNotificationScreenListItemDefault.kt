package com.baidaidai.rootless_store.components.thirdPartyNotificationScreen

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ThirdPartyNotificationScreenListItemDefault(
    modifier: Modifier = Modifier,
    headlineText: String,
    supportingText: String,
    leadingContent: @Composable (() -> Unit)? = null,
    checked: Boolean = false,
    onSwitchClicked: (Boolean)-> Unit = {},
    trailingContent: @Composable (()-> Unit)? = null
){
    ListItem(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp)),
        headlineContent = {
            Text(
                text = headlineText,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp,
                fontWeight = FontWeight(400)
            )
        },
        supportingContent = {
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                fontWeight = FontWeight(400)
            )
        },
        leadingContent = leadingContent,
        trailingContent = {
            if (trailingContent != null){
                trailingContent()
            }else{
                Switch(
                    checked = checked,
                    onCheckedChange = onSwitchClicked,
                )
            }
        },
        colors = ListItemColors()
    )
}

@Composable
private fun ListItemColors(): ListItemColors {
    return ListItemDefaults.colors().copy(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        disabledLeadingContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
        disabledTrailingContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
        disabledSupportingContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
    )
}