package com.baidaidai.rootless_store.ui.uistate

data class RootlessStoreThirdPartyNotificationScreenUiState(
    val barkApiKey: String = "",
    val notificationTitle:String? = null,
    val selfBuiltServer: String? = null,
    val isWarningNotificationEnabled: Boolean = false
)