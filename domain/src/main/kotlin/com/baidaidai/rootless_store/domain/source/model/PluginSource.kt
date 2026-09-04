package com.baidaidai.rootless_store.domain.source.model

data class PluginSource(
    val sourceRemoteEndpoint: String,
    val sourceId: String,
    val sourceName: String,
    val pluginSourceAuthenticationMetadata: PluginSourceAuthenticationMetadata
)