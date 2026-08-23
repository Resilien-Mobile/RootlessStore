package com.baidaidai.rootless_store.domain.source.model

data class PluginSourceInfo(
    val sourceRemoteEndpoint: String,
    val sourceId: String,
    val sourceName: String,
    val pluginSourceAuthenticationMeta: PluginSourceAuthenticationMeta
)