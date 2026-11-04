package com.baidaidai.rootless_store.ui.components.executeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.ui.R

@Composable
fun ShizukuPermissionDialog(
    errorCompanion: String,
    onGo: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.execute_screen_shizuku_permission_dialog_title))
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(errorCompanion)
            }
        },
        confirmButton = {
            Button(
                onClick = onGo
            ) {
                Text(stringResource(R.string.execute_screen_shizuku_permission_dialog_go))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.execute_screen_shizuku_permission_dialog_dismiss))
            }
        }
    )
}