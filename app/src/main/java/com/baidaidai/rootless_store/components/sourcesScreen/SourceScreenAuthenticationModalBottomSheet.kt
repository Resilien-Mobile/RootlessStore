package com.baidaidai.rootless_store.components.sourcesScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SourceScreenAuthenticationModalBottomSheet(
    onDismissRequest:()-> Unit = {},
    onDismissButtonClick: () -> Unit = {},
    onSubmitButtonClick: (userName:String, passWord: String) -> Unit = { u, p ->  }
){

    var userName by rememberSaveable { mutableStateOf("") }
    var passWord by rememberSaveable { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp, bottom = 24.dp)
            ,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Login Source",
                style = MaterialTheme.typography.titleLargeEmphasized
            )
            OutlinedTextField(
                value = userName,
                onValueChange = { userName = it},
                label = {
                    Text("Username")
                },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = passWord,
                onValueChange = { passWord = it },
                label = {
                    Text("Password")
                },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onDismissButtonClick,
                ) {
                    Text("Dismiss")
                }

                Spacer(
                    modifier = Modifier
                        .width(8.dp)
                )

                Button(
                    onClick = {
                        onSubmitButtonClick(userName,passWord)
                    },
                    modifier = Modifier
                        .height(40.dp)
                ) {
                    Text("Submit")
                }
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun _SourceScreenAuthenticationModalBottomSheetPreview_(){
    SourceScreenAuthenticationModalBottomSheet(
        onDismissRequest = {},
        onDismissButtonClick = {},
        onSubmitButtonClick = { u, p ->}
    )
}
