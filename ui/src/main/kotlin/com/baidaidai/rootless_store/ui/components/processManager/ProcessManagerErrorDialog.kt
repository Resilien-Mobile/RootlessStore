package com.baidaidai.rootless_store.ui.components.processManager

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.domain.process.error.ProcessError
import com.baidaidai.rootless_store.ui.R
import com.baidaidai.rootless_store.ui.components.common.ExpressiveDialog

@Composable
fun ProcessManagerErrorDialog(
    processError: ProcessError,
    onDismiss: () -> Unit
) {

    ExpressiveDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.start_screen_error_dialog_ok_button))
            }
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.material_symbols_warning),
                contentDescription = stringResource(
                    R.string.start_screen_error_dialog_warning_icon_content_description
                )
            )
        },
        title = {
            Text(processError.errorMessage)
        },
        enableSubTitle = !processError.errorCompanion.isNullOrEmpty(),
        subtitle = processError.errorCompanion?.let { errorCompanion ->
            {
                Text(
                    text = errorCompanion,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        enableContent = !processError.errorCause.isNullOrEmpty(),
        content = processError.errorCause?.let { errorCause ->
            {
                Text(
                    text = errorCause,
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState())
                )
            }
        }
    )

}
