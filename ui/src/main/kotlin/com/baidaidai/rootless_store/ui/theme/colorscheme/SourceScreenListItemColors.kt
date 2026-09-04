package com.baidaidai.rootless_store.ui.theme.colorscheme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun sourceListItemColors(): ListItemColors {
    val colorScheme = MaterialTheme.colorScheme
    return ListItemColors(
        containerColor = colorScheme.surfaceVariant,
        contentColor = colorScheme.onSurface,
        leadingContentColor = colorScheme.onSurfaceVariant,
        trailingContentColor = colorScheme.onSurfaceVariant,
        overlineContentColor = colorScheme.onSurfaceVariant,
        supportingContentColor = colorScheme.onSurfaceVariant,

        // Disabled：不做 copy，不在这里“造颜色”
        // 让组件在 enabled=false 时用 ContentAlpha.disabled 进行衰减
        disabledContainerColor = colorScheme.surfaceVariant,
        disabledContentColor = colorScheme.onSurface,
        disabledLeadingContentColor = colorScheme.onSurfaceVariant,
        disabledTrailingContentColor = colorScheme.onSurfaceVariant,
        disabledOverlineContentColor = colorScheme.onSurfaceVariant,
        disabledSupportingContentColor = colorScheme.onSurfaceVariant,

        // Selected：用 container/onContainer（不用 copy）
        selectedContainerColor = colorScheme.secondaryContainer,
        selectedContentColor = colorScheme.onSecondaryContainer,
        selectedLeadingContentColor = colorScheme.onSecondaryContainer,
        selectedTrailingContentColor = colorScheme.onSecondaryContainer,
        selectedOverlineContentColor = colorScheme.onSecondaryContainer,
        selectedSupportingContentColor = colorScheme.onSecondaryContainer,

        // Dragged：更像“浮起的那一行”，用 surface/onSurface（不用 copy）
        draggedContainerColor = colorScheme.surface,
        draggedContentColor = colorScheme.onSurface,
        draggedLeadingContentColor = colorScheme.onSurfaceVariant,
        draggedTrailingContentColor = colorScheme.onSurfaceVariant,
        draggedOverlineContentColor = colorScheme.onSurfaceVariant,
        draggedSupportingContentColor = colorScheme.onSurfaceVariant,
    )
}
