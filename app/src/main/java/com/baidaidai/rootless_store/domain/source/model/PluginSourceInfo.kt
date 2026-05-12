package com.baidaidai.rootless_store.domain.source.model

data class PluginSourceInfo(
    val sourceRemoteEndpoint: String,
    val sourceID: String,
    val sourceName: String,
    val pluginSourceAuthenticationMeta: PluginSourceAuthenticationMeta
)