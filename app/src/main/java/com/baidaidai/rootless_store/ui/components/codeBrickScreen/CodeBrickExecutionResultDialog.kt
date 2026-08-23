package com.baidaidai.rootless_store.ui.components.codeBrickScreen

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.R

@Composable
fun CodeBrickExecutionResultDialog(
    outputLines: List<String>,
    onDismissRequest: () -> Unit = {},
    onConfirmButtonClick: () -> Unit = {},
) {
    AlertDialog(
        title = {
            Text(stringResource(R.string.code_brick_screen_result_title))
        },
        confirmButton = {
            Button(
                onClick = onConfirmButtonClick
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

@Composable
@PreviewLightDark
private fun _CodeBrickExecutionResultDialogPreview_() {

}
