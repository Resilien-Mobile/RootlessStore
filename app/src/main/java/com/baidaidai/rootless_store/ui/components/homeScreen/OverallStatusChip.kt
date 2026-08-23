package com.baidaidai.rootless_store.ui.components.homeScreen

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.domain.status.model.RootlessStoreHosterStatus

@Composable
fun OverallStatusChip(
    hosterStatus: RootlessStoreHosterStatus,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
){
    Surface(
        modifier = modifier
            .scale(0.95f)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = AssistChipDefaults.shape,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Overall: ${hosterStatus.executionContext}",
                maxLines = 1,
                softWrap = false,
                style = MaterialTheme.typography.labelLarge,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun _preview_() {
    OverallStatusChip(
        hosterStatus = RootlessStoreHosterStatus(),
        onClick = {},
        onLongClick = {}
    )
}