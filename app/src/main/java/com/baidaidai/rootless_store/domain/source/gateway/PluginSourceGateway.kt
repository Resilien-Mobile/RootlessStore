package com.baidaidai.rootless_store.domain.source.gateway

import com.baidaidai.rootless_store.domain.source.model.PluginSourceInfo

interface PluginSourceGateway {
    suspend fun getPluginSource(
        sourceRemoteEndpoint: String
    ): PluginSourceInfo
}