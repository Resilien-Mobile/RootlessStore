package com.baidaidai.rootless_store.domain.source.model

import com.baidaidai.rootless_store.domain.error.RootlessStoreError

sealed interface PluginSourceEvent {

    data class SourceError(
        override val errorMessage: String,
        override val errorCause: String
    ): RootlessStoreError, PluginSourceEvent

    object SourceAuthentication: PluginSourceEvent
    object Success: PluginSourceEvent

}
