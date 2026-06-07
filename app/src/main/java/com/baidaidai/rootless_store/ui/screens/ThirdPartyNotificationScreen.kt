package com.baidaidai.rootless_store.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.components.thirdPartyNotificationScreen.ThirdPartyNotificationScreenListItemDefault
import com.baidaidai.rootless_store.ui.model.RootLessStoreThirdPartyNotificationScreenViewModel

@Composable
fun ThirdPartyNotificationScreen(
    contentPaddingValues: PaddingValues,
    thirdPartyNotificationScreenViewModel: RootLessStoreThirdPartyNotificationScreenViewModel
){

    val thirdPartyNotificationScreenUiState by thirdPartyNotificationScreenViewModel.thirdPartyNotificationScreenUiState.collectAsState()

    var apiKeyVisibility by rememberSaveable { mutableStateOf(false) }

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
                Text(stringResource(R.string.third_party_notification_screen_bark_api_key_input_label))
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        apiKeyVisibility = !apiKeyVisibility
                    }
                ) {
                    if (apiKeyVisibility){
                        Icon(
                            painter = painterResource(R.drawable.material_symbols_visibility),
                            contentDescription = stringResource(R.string.third_party_notification_screen_api_key_visibility_content_description)
                        )
                    }else{
                        Icon(
                            painter = painterResource(R.drawable.material_symbols_visibility_off),
                            contentDescription = stringResource(R.string.third_party_notification_screen_api_key_visibility_off_content_description)
                        )
                    }
                }
            },
            visualTransformation = if (apiKeyVisibility) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            }
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = thirdPartyNotificationScreenUiState.notificationTitle ?: "",
            onValueChange = thirdPartyNotificationScreenViewModel::onNotificationTitleChanged,
            label = {
                Text(stringResource(R.string.third_party_notification_screen_notification_title_input_label))
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        thirdPartyNotificationScreenViewModel.onNotificationTitleChanged(null)
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_close_24),
                        contentDescription = stringResource(R.string.third_party_notification_screen_clear_content_description)
                    )
                }
            },
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = thirdPartyNotificationScreenUiState.selfBuiltServer ?: "",
            onValueChange = thirdPartyNotificationScreenViewModel::onSelfBuiltServerChanged,
            label = {
                Text(stringResource(R.string.third_party_notification_screen_self_built_server_input_label))
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        thirdPartyNotificationScreenViewModel.onSelfBuiltServerChanged(null)
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_close_24),
                        contentDescription = stringResource(R.string.third_party_notification_screen_clear_content_description)
                    )
                }
            },
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
        ) {
            ThirdPartyNotificationScreenListItemDefault(
                headlineText = stringResource(R.string.third_party_notification_screen_warning_notification_headline),
                supportingText = stringResource(R.string.third_party_notification_screen_warning_notification_supporting),
                trailingContent = {
                    Switch(
                        checked = thirdPartyNotificationScreenUiState.warningNotificationEnabled,
                        onCheckedChange = thirdPartyNotificationScreenViewModel::onWarningNotificationEnabledChanged
                    )
                },
                leadingContent = {
                    Icon(
                        painter = painterResource(R.drawable.material_symbols_warning),
                        contentDescription = stringResource(R.string.third_party_notification_screen_warning_notification_icon_content_description)
                    )
                }
            )
        }
    }
}
