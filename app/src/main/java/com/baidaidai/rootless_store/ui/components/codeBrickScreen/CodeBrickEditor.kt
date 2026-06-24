package com.baidaidai.rootless_store.ui.components.codeBrickScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeBrickEditor(
    onDismissRequest: () -> Unit,
    onDismissButtonClick: ()-> Unit,
    onConfirmButtonClick: (title: String,content: String)-> Unit
){

    var titleContent by remember { mutableStateOf("") }
    var codeContent by remember { mutableStateOf("") }

    AlertDialog(
        title = {
            Text("Code Brick")
        },
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(
                onClick = onDismissButtonClick
            ) {
                Text("Cancel")
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmButtonClick(titleContent,codeContent) }
            ) {
                Text("Confirm")
            }
        },
        text = {
            Column() {

                OutlinedTextField(
                    value = titleContent,
                    onValueChange = { titleContent = it },
                    label = {
                        Text("Brick Name")
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = codeContent,
                    onValueChange = { codeContent = it },
                    label = {
                        Text("Shell Script")
                    }
                )

            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}