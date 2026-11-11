package com.baidaidai.rootless_store.domain.process.error

import com.baidaidai.rootless_store.domain.error.RootlessStoreError

data class ProcessError(
    override val errorMessage: String,
    override val errorCause: String? = null,
    override val errorCompanion: String? = null
) : RootlessStoreError
