package com.baidaidai.rootless_store.ui.uistate

import com.baidaidai.rootless_store.domain.install.model.PackageSignatureInfo

data class RootlessStorePluginScreenUiState(
    val packageSignatureInfo: PackageSignatureInfo? = null,
    val isLocalPackageConfirmationSheetVisible: Boolean = false
)
