package com.baidaidai.rootless_store.ui.components.pluginScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.ui.R
import com.baidaidai.rootless_store.ui.components.common.VerticalSpacer

@Composable
fun PluginCaCertificateBottomSheetContent(
    modifier: Modifier = Modifier,
    certificateName: String,
    onCertificateNameChange: (String) -> Unit,
    onSelectCertificateFile: () -> Unit = {},
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = certificateName,
            onValueChange = onCertificateNameChange,
            label = {
                Text(stringResource(R.string.plugin_ca_certificate_name_label))
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        VerticalSpacer(20.dp)

        OutlinedButton(
            onClick = onSelectCertificateFile,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.plugin_ca_certificate_select_file_button))
        }

        VerticalSpacer(50.dp)

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onDismiss,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.plugin_ca_certificate_dismiss_button))
            }
            Button(
                onClick = onConfirm,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.plugin_ca_certificate_confirm_button))
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun _preview_() {
    PluginCaCertificateBottomSheetContent(
        certificateName = "",
        onCertificateNameChange = {},
        onDismiss = {},
        onSelectCertificateFile = {}
    )
}