package com.baidaidai.rootless_store.domain.plugin.error

import com.baidaidai.rootless_store.domain.error.RootlessStoreError

data class PluginError(
    override val errorMessage: String,
    override val errorCause: String
): RootlessStoreError