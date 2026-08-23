package com.baidaidai.rootless_store.domain.setting.model

data class SettingScreenPreference(
    val isAutoUpdateEnabled: Boolean = false,
    val isPluginStatusNotificationEnabled: Boolean = false,
    val isThirdPartyNotificationPushEnabled: Boolean = false,
    val isInsecureConnectionAllowed: Boolean = false,
    val isDotProtectedConnectionEnabled: Boolean = false
)
