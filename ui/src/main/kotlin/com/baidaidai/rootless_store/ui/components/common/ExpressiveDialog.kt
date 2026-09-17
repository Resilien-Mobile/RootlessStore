package com.baidaidai.rootless_store.ui.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExpressiveDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    enableSubTitle: Boolean = false,
    subtitle: @Composable (()-> Unit)? = null,
    enableContent: Boolean = false,
    content: @Composable (() -> Unit)? = null
){
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        icon = icon,
        title = title,
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {

                if (enableSubTitle){
                    subtitle?.invoke()

                    if (enableContent){
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                content?.invoke()
            }
        }
    )
}
