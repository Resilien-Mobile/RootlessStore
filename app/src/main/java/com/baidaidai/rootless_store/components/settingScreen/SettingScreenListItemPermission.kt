package com.baidaidai.rootless_store.components.settingScreen

import com.baidaidai.rootless_store.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun SettingScreenListItemPermission(
    modifier: Modifier = Modifier,
    headlineText: String,
    supportingText: String,
    leadingContent: @Composable (() -> Unit)? = null,
    onIconButtonClick: ()-> Unit = {}
){
    ListItem(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onIconButtonClick),
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
            IconButton(
                onClick = onIconButtonClick
            ) {
                Icon(
                    painter = painterResource(R.drawable.material_symbols_arrow_outward),
                    contentDescription = stringResource(R.string.setting_screen_permission_open_content_description)
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
