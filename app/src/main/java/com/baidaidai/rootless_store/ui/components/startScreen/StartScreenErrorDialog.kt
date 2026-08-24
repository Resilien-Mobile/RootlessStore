package com.baidaidai.rootless_store.ui.components.startScreen

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
import com.baidaidai.rootless_store.ui.model.RootlessStoreSourceScreenViewModel
import com.baidaidai.rootless_store.ui.model.RootlessStoreShizukuAdbScreenViewModel

@Composable
fun StartScreenErrorDialog(
    sourceScreenViewModel: RootlessStoreSourceScreenViewModel,
    error: RootlessStoreError?
){
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            Button(
                onClick = {
                    sourceScreenViewModel.dismissSourceError()
                }
            ) {
                Text("Ok")
            }
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.material_symbols_warning),
                contentDescription = "Dialog Warning Logo"
            )
        },
        title = {
            Text(error!!.errorMessage)
        },
        text = {
            Text(
                text = error!!.errorCause,
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
    error: RootlessStoreError?
){
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            Button(
                onClick = {
                    shizukuAdbScreenViewModel.dismissShizukuError()
                }
            ) {
                Text("Ok")
            }
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.material_symbols_warning),
                contentDescription = "Dialog Warning Logo"
            )
        },
        title = {
            Text(error!!.errorMessage)
        },
        text = {
            Text(
                text = error!!.errorCause,
                modifier = Modifier
                    .heightIn(max = 300.dp)
                    .verticalScroll(
                        state = rememberScrollState()
                    )
            )
        }
    )
}
