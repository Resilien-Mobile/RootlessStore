package com.baidaidai.rootless_store.ui.components.codeBrickScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CodeBrickEditorContent(
    modifier: Modifier = Modifier,
    titleContent: String,
    codeContent: String,
    onCodeBrickTitleValueChange: (value: String)-> Unit,
    onCodeBrickContentValueChange: (value: String)-> Unit,
    onCodeBrickContextValueChange: (hosterOverallStatus: HosterOverallStatus) -> Unit
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
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        leadingContentColor = MaterialTheme.colorScheme.onSurface,
        trailingContentColor = MaterialTheme.colorScheme.onSurface,
        overlineContentColor = MaterialTheme.colorScheme.onSurface,
        supportingContentColor = MaterialTheme.colorScheme.onSurface,
        disabledContainerColor = MaterialTheme.colorScheme.surface,
        disabledContentColor = MaterialTheme.colorScheme.onSurface,
        disabledLeadingContentColor = MaterialTheme.colorScheme.onSurface,
        disabledTrailingContentColor = MaterialTheme.colorScheme.onSurface,
        disabledOverlineContentColor = MaterialTheme.colorScheme.onSurface,
        disabledSupportingContentColor = MaterialTheme.colorScheme.onSurface,
        selectedContainerColor = MaterialTheme.colorScheme.surface,
        selectedContentColor = MaterialTheme.colorScheme.onSurface,
        selectedLeadingContentColor = MaterialTheme.colorScheme.onSurface,
        selectedTrailingContentColor = MaterialTheme.colorScheme.onSurface,
        selectedOverlineContentColor = MaterialTheme.colorScheme.onSurface,
        selectedSupportingContentColor = MaterialTheme.colorScheme.onSurface,
        draggedContainerColor = MaterialTheme.colorScheme.surface,
        draggedContentColor = MaterialTheme.colorScheme.onSurface,
        draggedLeadingContentColor = MaterialTheme.colorScheme.onSurface,
        draggedTrailingContentColor = MaterialTheme.colorScheme.onSurface,
        draggedOverlineContentColor = MaterialTheme.colorScheme.onSurface,
        draggedSupportingContentColor = MaterialTheme.colorScheme.onSurface,
    )

    val brickContextConfigList = listOf(
        CodeBrickContextConfig(
            contextType = HosterOverallStatus.LIMITED,
            contextTextResource = R.string.code_brick_screen_editor_context_app_shell_label,
            contextIcon = R.drawable.material_symbols_applicaitons
        ),
        CodeBrickContextConfig(
            contextType = HosterOverallStatus.ADB,
            contextTextResource = R.string.code_brick_screen_editor_context_adb_shell_label,
            contextIcon = R.drawable.material_symbols_adb
        ),
        CodeBrickContextConfig(
            contextType = HosterOverallStatus.ROOTD,
            contextTextResource = R.string.code_brick_screen_editor_context_root_shell_label,
            contextIcon = R.drawable.material_symbols_cyclone
        )
    )
    var currentSelectedContext by remember { mutableStateOf(brickContextConfigList[0]) }

    fun listItemColors(index: Int): ListItemColors{
        return if (brickContextConfigList[index].contextType == currentSelectedContext.contextType){
            focusedListItemStyle
        }else{
            unfocusedListItemStyle
        }
    }

    Column(modifier) {

        OutlinedTextField(
            value = titleContent,
            onValueChange = onCodeBrickTitleValueChange,
            label = {
                Text(stringResource(R.string.code_brick_screen_editor_brick_name_label))
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = codeContent,
            onValueChange = onCodeBrickContentValueChange,
            label = {
                Text(stringResource(R.string.code_brick_screen_editor_shell_script_label))
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.code_brick_screen_editor_brick_context_title),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        brickContextConfigList.forEachIndexed { index, codeBrickContextConfig ->
            val contextText = stringResource(codeBrickContextConfig.contextTextResource)

            ListItem(
                onClick = {
                    currentSelectedContext = codeBrickContextConfig
                    onCodeBrickContextValueChange(codeBrickContextConfig.contextType)
                },
                leadingContent = {
                    Icon(
                        painter = painterResource(codeBrickContextConfig .contextIcon),
                        contentDescription = contextText
                    )
                },
                colors = listItemColors(index),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Text(contextText)
            }

            if (index != 2){
                Spacer(Modifier.height(2.dp))
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeBrickEditor(
    onDismissRequest: () -> Unit,
    onDismissButtonClick: ()-> Unit,
    onConfirmButtonClick: (title: String,content: String,context: HosterOverallStatus)-> Unit
){

    var titleContent by remember { mutableStateOf("") }
    var codeContent by remember { mutableStateOf("") }
    var selectedContext by remember { mutableStateOf(HosterOverallStatus.LIMITED) }

    AlertDialog(
        title = {
            Text(stringResource(R.string.code_brick_screen_editor_title))
        },
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(
                onClick = onDismissButtonClick
            ) {
                Text(stringResource(R.string.code_brick_screen_editor_cancel_button))
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmButtonClick(titleContent,codeContent, selectedContext) }
            ) {
                Text(stringResource(R.string.code_brick_screen_editor_confirm_button))
            }
        },
        text = {
            CodeBrickEditorContent(
                titleContent = titleContent,
                codeContent = codeContent,
                onCodeBrickTitleValueChange = { titleContent = it },
                onCodeBrickContentValueChange = { codeContent = it },
                onCodeBrickContextValueChange = { selectedContext = it }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@PreviewLightDark
@Composable
private fun _preview_(){
    CodeBrickEditorContent(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .width(280.dp)
            .heightIn(min = 300.dp)
        ,
        titleContent = "a",
        codeContent =  "b",
        onCodeBrickTitleValueChange =  {},
        onCodeBrickContentValueChange = {},
        onCodeBrickContextValueChange = {}
    )
}