package com.baidaidai.rootless_store.components.startScreen

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.domain.error.RootlessStoreError
import com.baidaidai.rootless_store.ui.model.RootLessStoreSourceScreenViewModel
import com.baidaidai.rootless_store.ui.model.RootlessStoreShizukuAdbScreenViewModel

@Composable
fun StartScreenErrorDialog(
    sourceScreenViewModel: RootLessStoreSourceScreenViewModel,
    sharedEvent: RootlessStoreError?
){
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            Button(
                onClick = {
                    sourceScreenViewModel.onOkButtonClick()
                }
            ) {
                Text("Ok")
            }
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.material_symbols_warning_24px),
                contentDescription = "Dialog Warning Logo"
            )
        },
        title = {
            Text(sharedEvent!!.errorMessage)
        },
        text = {
            Text(
                text = sharedEvent!!.errorCause,
                modifier = Modifier
                    .heightIn(max = 300.dp)
                    .verticalScroll(
                        state = rememberScrollState()
                    )
            )
        }
    )
}
@Composable
fun StartScreenErrorDialog(
    shizukuAdbScreenViewModel: RootlessStoreShizukuAdbScreenViewModel,
    sharedEvent: RootlessStoreError?
){
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            Button(
                onClick = {
                    shizukuAdbScreenViewModel.onOkButtonClick()
                }
            ) {
                Text("Ok")
            }
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.material_symbols_warning_24px),
                contentDescription = "Dialog Warning Logo"
            )
        },
        title = {
            Text(sharedEvent!!.errorMessage)
        },
        text = {
            Text(
                text = sharedEvent!!.errorCause,
                modifier = Modifier
                    .heightIn(max = 300.dp)
                    .verticalScroll(
                        state = rememberScrollState()
                    )
            )
        }
    )
}