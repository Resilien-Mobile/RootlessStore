package com.baidaidai.rootless_store.ui.components.codeBrickScreen

import android.content.ClipData
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.ui.R
import kotlinx.coroutines.launch

@Composable
fun CodeBrickExecutionResultDialog(
    outputLines: List<String>,
    onDismissRequest: () -> Unit = {},
    onDismissClick: () -> Unit = {},
) {

    val clipboard = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        title = {
            Text(stringResource(R.string.code_brick_screen_result_title))
        },
        dismissButton = {
            TextButton(
                onClick = {
                    coroutineScope.launch {
                        clipboard.setClipEntry(
                            ClipEntry(
                                ClipData.newPlainText(
                                    "Code Brick execution result",
                                    outputLines.joinToString("\n")
                                )
                            )
                        )
                        onDismissRequest()
                    }
                }
            ) {
                Text("Copy")
            }
        },
        confirmButton = {
            Button(
                onClick = onDismissClick
            ) {
                Text(stringResource(R.string.code_brick_screen_result_confirm_button))
            }
        },
        onDismissRequest = onDismissRequest,
        text = {
            LazyColumn {
                itemsIndexed(
                    items = outputLines
                ){ _, outputLine ->
                    Text(outputLine)
                }
            }
        },
        modifier = Modifier
            .heightIn(max = 400.dp)
    )
}
