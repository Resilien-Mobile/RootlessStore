package com.baidaidai.rootless_store.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.ui.model.RootLessStoreThirdPartyNotificationScreenViewModel

@Composable
fun ThirdPartyNotificationScreen(
    contentPaddingValues: PaddingValues,
    thirdPartyNotificationScreenViewModel: RootLessStoreThirdPartyNotificationScreenViewModel
){

    val thirdPartyNotificationScreenUiState by thirdPartyNotificationScreenViewModel.thirdPartyNotificationScreenUiState.collectAsState()

    Column(
        modifier = Modifier
            .padding(contentPaddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = thirdPartyNotificationScreenUiState.barkApiKey,
            onValueChange = thirdPartyNotificationScreenViewModel::onBarkApiKeyChanged,
            label = {
                Text("Bark APP API KEY")
            }
        )
        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            headlineContent = {
                Text(
                    text = "Warning Notification",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            supportingContent = {
                Text(
                    text = "Enable warning notification",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            trailingContent = {
                Switch(
                    checked = thirdPartyNotificationScreenUiState.warningNotificationEnabled,
                    onCheckedChange = thirdPartyNotificationScreenViewModel::onWarningNotificationEnabledChanged
                )
            }
        )
    }
}
