package com.baidaidai.rootless_store.ui.screens

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.components.settingScreen.SettingScreenListItemDefault
import com.baidaidai.rootless_store.components.settingScreen.SettingScreenListItemPermission
import com.baidaidai.rootless_store.ui.model.RootLessStoreSettingScreenViewModel
import androidx.core.net.toUri


@Composable
fun SettingScreen(
    contentPaddingValues: PaddingValues,
    settingScreenViewModel: RootLessStoreSettingScreenViewModel = hiltViewModel(),
    onThirdPartyNotificationSettingClick: ()-> Unit
){

    val settingPanelPreferences by settingScreenViewModel.settingPanelPreferences.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .padding(contentPaddingValues),
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 16.dp
        )
    ) {
        item {
            Text(
                modifier = Modifier
                    .padding(6.dp),
                text = stringResource(R.string.setting_screen_section_general_title),
                style = MaterialTheme.typography.titleMedium
            )
        }
        item {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
            ) {
                SettingScreenListItemDefault(
                    headlineText = stringResource(R.string.setting_screen_general_check_latest_version_headline),
                    supportingText = stringResource(R.string.setting_screen_general_check_latest_version_supporting),
                    checked = settingPanelPreferences.enableAutoUpdate,
                    onSwitchClicked = settingScreenViewModel::setEnableAutoUpdate,
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.material_symbols_upgrade),
                            contentDescription = stringResource(R.string.setting_screen_general_check_latest_version_icon_content_description)
                        )
                    }
                )
                Spacer(modifier = Modifier.height(2.dp))
                SettingScreenListItemDefault(
                    headlineText = stringResource(R.string.setting_screen_general_notify_plugin_status_headline),
                    supportingText = stringResource(R.string.setting_screen_general_notify_plugin_status_supporting),
                    checked = settingPanelPreferences.notifyPluginStatus,
                    onSwitchClicked = settingScreenViewModel::setNotifyPluginStatus,
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.material_symbols_notification),
                            contentDescription = stringResource(R.string.setting_screen_general_notify_plugin_status_icon_content_description)
                        )
                    }
                )
                Spacer(modifier = Modifier.height(2.dp))
                SettingScreenListItemDefault(
                    headlineText = stringResource(R.string.setting_screen_general_third_party_push_headline),
                    supportingText = stringResource(R.string.setting_screen_general_third_party_push_supporting),
                    checked = settingPanelPreferences.useThirdPartyNotificationPush,
                    onSwitchClicked = settingScreenViewModel::setUseThirdPartyNotificationPush,
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.material_symbols_notification_add),
                            contentDescription = stringResource(R.string.setting_screen_general_third_party_push_icon_content_description)
                        )
                    },
                    trailingContent = {
                        IconButton(
                            onClick = onThirdPartyNotificationSettingClick
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.material_symbols_settings),
                                contentDescription = "setting"
                            )
                        }
                    }
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Text(
                modifier = Modifier
                    .padding(6.dp),
                text = stringResource(R.string.setting_screen_section_source_title),
                style = MaterialTheme.typography.titleMedium
            )
        }
        item {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
            ) {
                SettingScreenListItemDefault(
                    headlineText = stringResource(R.string.setting_screen_source_allow_insecure_connection_headline),
                    supportingText = stringResource(R.string.setting_screen_source_allow_insecure_connection_supporting),
                    checked = settingPanelPreferences.allowInsecureConnection,
                    onSwitchClicked = settingScreenViewModel::setAllowInsecureConnection,
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.material_symbols_safety_check),
                            contentDescription = stringResource(R.string.setting_screen_source_allow_insecure_connection_icon_content_description)
                        )
                    }
                )
                Spacer(modifier = Modifier.height(2.dp))
                SettingScreenListItemDefault(
                    headlineText = stringResource(R.string.setting_screen_source_use_dot_protected_connection_headline),
                    supportingText = stringResource(R.string.setting_screen_source_use_dot_protected_connection_supporting),
                    checked = settingPanelPreferences.useDotProtectedConnection,
                    onSwitchClicked = settingScreenViewModel::setUseDotProtectedConnection,
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.material_symbols_cloud_lock),
                            contentDescription = stringResource(R.string.setting_screen_source_use_dot_protected_connection_icon_content_description)
                        )
                    }
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text(
                modifier = Modifier
                    .padding(6.dp),
                text = stringResource(R.string.setting_screen_section_permission_title),
                style = MaterialTheme.typography.titleMedium
            )
        }
        item {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
            ) {
                SettingScreenListItemPermission(
                    headlineText = stringResource(R.string.setting_screen_permission_all_files_access_headline),
                    supportingText = stringResource(R.string.setting_screen_permission_all_files_access_supporting),
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.material_symbols_folder_managed),
                            contentDescription = stringResource(R.string.setting_screen_permission_all_files_access_icon_content_description)
                        )
                    },
                    onIconButtonClick = { SettingPermission.jumpToAllFilesAccess(context) }
                )
                Spacer(modifier = Modifier.height(2.dp))
                SettingScreenListItemPermission(
                    headlineText = stringResource(R.string.setting_screen_permission_stop_restrict_child_process_headline),
                    supportingText = stringResource(R.string.setting_screen_permission_stop_restrict_child_process_supporting),
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.material_symbols_do_not_touch),
                            contentDescription = stringResource(R.string.setting_screen_permission_stop_restrict_child_process_icon_content_description)
                        )
                    },
                    onIconButtonClick = { SettingPermission.jumpToStopRestrictChildProcess(context) }
                )
                Spacer(modifier = Modifier.height(2.dp))
                SettingScreenListItemPermission(
                    headlineText = stringResource(R.string.setting_screen_permission_notification_headline),
                    supportingText = stringResource(R.string.setting_screen_permission_notification_supporting),
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.material_symbols_notification_settings),
                            contentDescription = stringResource(R.string.setting_screen_permission_notification_icon_content_description)
                        )
                    },
                    onIconButtonClick = { SettingPermission.jumpToNotificationPermission(context) }
                )
                Spacer(modifier = Modifier.height(2.dp))
                SettingScreenListItemPermission(
                    headlineText = stringResource(R.string.setting_screen_permission_battery_headline),
                    supportingText = stringResource(R.string.setting_screen_permission_battery_supporting),
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.material_symbols_battery_profile),
                            contentDescription = stringResource(R.string.setting_screen_permission_battery_icon_content_description)
                        )
                    },
                    onIconButtonClick = { SettingPermission.jumpToBatteryPermission(context) }
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text(
                text = stringResource(R.string.setting_screen_section_about_title),
                style = MaterialTheme.typography.titleMedium
            )
        }
        item {
            ListItem(
                headlineContent = {
                    Text(stringResource(R.string.setting_screen_about_rootless_store_headline))
                }
            )
        }
    }
}

private object SettingPermission {
    fun jumpToAllFilesAccess(context: Context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = "package:${context.packageName}".toUri()
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    fun jumpToStopRestrictChildProcess(context: Context){
        val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun jumpToNotificationPermission(context: Context){
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun jumpToBatteryPermission(context: Context){
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = "package:${context.packageName}".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
