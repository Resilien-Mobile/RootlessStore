package com.baidaidai.rootless_store.domain.source.gateway

import com.baidaidai.rootless_store.domain.source.model.PluginSource
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthenticationInput
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthenticationResult

interface PluginSourceGateway {

    suspend fun fetchPluginSource(
        sourceRemoteEndpoint: String
    ): PluginSource

    suspend fun fetchPluginSourceAuthenticationResult(
        authenticationInput: PluginSourceAuthenticationInput
    ): PluginSourceAuthenticationResult

}
