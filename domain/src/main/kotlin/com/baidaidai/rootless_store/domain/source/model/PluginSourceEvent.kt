package com.baidaidai.rootless_store.domain.source.model

import com.baidaidai.rootless_store.domain.error.RootlessStoreError

sealed interface PluginSourceEvent {

    data class SourceError(
        override val errorMessage: String,
        override val errorCause: String,
        override val errorCompanion: String? = null
    ): RootlessStoreError, PluginSourceEvent

    object AuthenticationRequired: PluginSourceEvent
    object Success: PluginSourceEvent

}
