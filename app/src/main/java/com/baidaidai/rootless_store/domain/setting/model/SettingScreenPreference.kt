package com.baidaidai.rootless_store.domain.setting.model

data class SettingScreenPreference(
    val notifyPluginStatus: Boolean = false,
    val useThirdPartyNotificationPush: Boolean = false,
    val allowInsecureConnection: Boolean = false,
    val useDotProtectedConnection: Boolean = false
)
