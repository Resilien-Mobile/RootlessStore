package com.baidaidai.rootless_store.domain.assistant.error

import com.baidaidai.rootless_store.domain.error.RootlessStoreError

data class AssistantError(
    override val errorMessage: String,
    override val errorCause: String? = null,
    override val errorCompanion: String? = null
): RootlessStoreError
