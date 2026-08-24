package com.baidaidai.rootless_store.ui.components.codeBrickScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickContextConfig
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CodeBrickContextList(
    modifier: Modifier = Modifier,
    selectedContext: CodeBrickContextConfig,
    codeBrickContexts: List<CodeBrickContextConfig>,
    onContextSelected: (codeBrickContextConfig: CodeBrickContextConfig)-> Unit
){

    val focusedListItemStyle = ListItemColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        leadingContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        trailingContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        overlineContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        supportingContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
        disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        disabledLeadingContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        disabledTrailingContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        disabledOverlineContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        disabledSupportingContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
        selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        selectedLeadingContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        selectedTrailingContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        selectedOverlineContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        selectedSupportingContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        draggedContainerColor = MaterialTheme.colorScheme.primaryContainer,
        draggedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        draggedLeadingContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        draggedTrailingContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        draggedOverlineContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        draggedSupportingContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
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

    var isContextListExpanded by remember { mutableStateOf(false) }

    fun contextListItemColors(index: Int): ListItemColors{
        return if (codeBrickContexts[index].contextType == selectedContext.contextType){
            focusedListItemStyle
        }else{
            unfocusedListItemStyle
        }
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
    ) {
        ListItem(
            onClick = { isContextListExpanded = !isContextListExpanded },
            trailingContent = {
                Icon(
                    painter = painterResource(R.drawable.material_symbols_keyboard_arrow_down_icon),
                    contentDescription = "Expand More"
                )
            },
            colors = unfocusedListItemStyle
        ){
            Text(
                text = stringResource(R.string.code_brick_screen_editor_brick_context_title),
                style = MaterialTheme.typography.titleMedium
            )
        }
        if (isContextListExpanded){
            Spacer(modifier = Modifier.height(2.dp))
            codeBrickContexts.forEachIndexed { index, codeBrickContextConfig ->
                val contextText = stringResource(codeBrickContextConfig.contextTextResource)

                ListItem(
                    onClick = { onContextSelected(codeBrickContextConfig) },
                    leadingContent = {
                        Icon(
                            painter = painterResource(codeBrickContextConfig .contextIcon),
                            contentDescription = contextText
                        )
                    },
                    colors = contextListItemColors(index),
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                ) {
                    Text(contextText)
                }

                if (index != 2){
                    Spacer(Modifier.height(2.dp))
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun _CodeBrickContextListPreview_() {

    val codeBrickContexts = listOf(
        CodeBrickContextConfig(
            contextType = ExecutionContext.LIMITED,
            contextTextResource = R.string.code_brick_screen_editor_context_app_shell_label,
            contextIcon = R.drawable.material_symbols_applications
        ),
        CodeBrickContextConfig(
            contextType = ExecutionContext.ADB,
            contextTextResource = R.string.code_brick_screen_editor_context_adb_shell_label,
            contextIcon = R.drawable.material_symbols_adb
        ),
        CodeBrickContextConfig(
            contextType = ExecutionContext.ROOTD,
            contextTextResource = R.string.code_brick_screen_editor_context_root_shell_label,
            contextIcon = R.drawable.material_symbols_cyclone
        )
    )

    var selectedContext by remember { mutableStateOf(codeBrickContexts[0]) }

    CodeBrickContextList(
        selectedContext = selectedContext,
        codeBrickContexts = codeBrickContexts,
        modifier = Modifier
            .width(300.dp)
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) { }
}
