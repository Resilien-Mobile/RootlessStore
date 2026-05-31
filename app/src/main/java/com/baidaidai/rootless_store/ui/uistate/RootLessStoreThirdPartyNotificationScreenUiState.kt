package com.baidaidai.rootless_store.ui.uistate

data class RootLessStoreThirdPartyNotificationScreenUiState(
    val barkApiKey: String = "",
    val notificationTitle:String? = null,
    val selfBuiltServer: String? = null,
    val warningNotificationEnabled: Boolean = false
)