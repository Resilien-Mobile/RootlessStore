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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp

@Composable
fun CodeBrickResult(
    resultContent: List<String>,
    onDismissRequest: () -> Unit = {},
    onConfirmButtonClick: () -> Unit = {},
) {
    AlertDialog(
        title = {
            Text("Result")
        },
        confirmButton = {
            Button(
                onClick = onConfirmButtonClick
            ) {
                Text("Confirm")
            }
        },
        onDismissRequest = onDismissRequest,
        text = {
            LazyColumn {
                itemsIndexed(
                    items = resultContent
                ){ index, content ->
                    Text(content)
                }
            }
        },
        modifier = Modifier
            .heightIn(max = 400.dp)
    )
}

@Composable
@PreviewLightDark
private fun _CodeBrickResultPreview_() {

}
