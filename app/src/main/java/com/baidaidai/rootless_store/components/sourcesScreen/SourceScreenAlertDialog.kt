package com.baidaidai.rootless_store.components.sourcesScreen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark

@Composable
fun SourceScreenAlertDialog(
    onDismissRequest: () -> Unit = {},
    onDismissButtonClick: () -> Unit = {},
    onConfirmButtonClick: () -> Unit = {}
){
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = onConfirmButtonClick
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissButtonClick
            ) {
                Text("Cancel")
            }
        },
        title = {
            Text("Need Authentication")
        },
        text = {
            Text(
                "我们会打开一个小窗口，用于完成一次简短的认证流程，通常很快就能结束\n" +
                    "你也可以随时取消本次添加"
            )
        }
    )
}

@Composable
@PreviewLightDark
private fun _SourceScreenAlertDialogPreview_(){
    SourceScreenAlertDialog(
        onDismissRequest = {},
        onDismissButtonClick = {},
        onConfirmButtonClick = {}
    )
}
