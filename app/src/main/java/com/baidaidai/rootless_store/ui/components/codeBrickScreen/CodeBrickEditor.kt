package com.baidaidai.rootless_store.ui.components.codeBrickScreen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeBrickEditor(
    onDismissRequest: () -> Unit,
    onDismissButtonClick: ()-> Unit,
    onConfirmButtonClick: (title: String,content: String,context: HosterOverallStatus,tileIndex: Int?)-> Unit
){

    var titleContent by remember { mutableStateOf("") }
    var codeContent by remember { mutableStateOf("") }
    var tileIndex: Int? by remember { mutableStateOf(null) }
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
                onClick = { onConfirmButtonClick(titleContent,codeContent, selectedContext, tileIndex) }
            ) {
                Text(stringResource(R.string.code_brick_screen_editor_confirm_button))
            }
        },
        text = {
            CodeBrickEditorContent(
                titleContent = titleContent,
                codeContent = codeContent,
                tileIndex = tileIndex,
                onCodeBrickTitleValueChange = { titleContent = it },
                onCodeBrickContentValueChange = { codeContent = it },
                onCodeBrickContextValueChange = { selectedContext = it },
                onCodeBrickTileValueChange = { tileIndex = it }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}