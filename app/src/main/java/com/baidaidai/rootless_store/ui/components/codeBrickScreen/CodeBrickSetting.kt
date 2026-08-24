package com.baidaidai.rootless_store.ui.components.codeBrickScreen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeBrickSetting(
    codeBrickConfig: CodeBrickConfig,
    onDismissRequest: () -> Unit,
    onCancelClick: ()-> Unit,
    onUpdateClick: (
        title: String,
        content: String,
        context: ExecutionContext,
        tileIndex: Int?,
        oldCodeBrickConfig: CodeBrickConfig
    )-> Unit,
    onInstallPluginClick: (codeBrickConfig: CodeBrickConfig)-> Unit
){

    var titleContent by remember { mutableStateOf("") }
    var codeContent by remember { mutableStateOf("") }
    var tileIndex: Int? by remember { mutableStateOf(null) }
    var selectedContext by remember { mutableStateOf(ExecutionContext.LIMITED) }

    LaunchedEffect(codeBrickConfig) {
        titleContent = codeBrickConfig.codeBrickTitle
        codeContent = codeBrickConfig.codeBrickContent
        tileIndex = codeBrickConfig.boundTileIndex
        selectedContext = codeBrickConfig.codeBrickEnvironment
    }

    AlertDialog(
        title = {
            Text(stringResource(R.string.code_brick_screen_editor_title))
        },
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(
                onClick = onCancelClick
            ) {
                Text(stringResource(R.string.code_brick_screen_editor_cancel_button))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onUpdateClick(
                        titleContent,
                        codeContent,
                        selectedContext,
                        tileIndex,
                        codeBrickConfig
                    )
                }
            ) {
                Text(stringResource(R.string.code_brick_screen_editor_confirm_button))
            }
        },
        text = {
            CodeBrickSettingContent(
                titleContent = titleContent,
                codeContent = codeContent,
                tileIndex = tileIndex,
                brickContext = selectedContext,
                onCodeBrickTitleValueChange = { titleContent = it },
                onCodeBrickContentValueChange = { codeContent = it },
                onCodeBrickContextValueChange = { selectedContext = it },
                onCodeBrickTileValueChange = { tileIndex = it },
                onInstallPluginClick = { onInstallPluginClick(codeBrickConfig) }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}
