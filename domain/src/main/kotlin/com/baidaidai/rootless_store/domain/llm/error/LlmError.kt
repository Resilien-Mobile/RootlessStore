package com.baidaidai.rootless_store.domain.llm.error

import com.baidaidai.rootless_store.domain.error.RootlessStoreError

data class LlmError(
    override val errorMessage: String,
    override val errorCause: String,
    override val errorCompanion: String? = null
): RootlessStoreError
