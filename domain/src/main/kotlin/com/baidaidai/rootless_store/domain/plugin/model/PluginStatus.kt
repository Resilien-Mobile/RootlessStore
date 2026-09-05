package com.baidaidai.rootless_store.domain.plugin.model

data class PluginStatus(
    val pluginId: String,
    val isEnabled: Boolean,
    val state: PluginState,
    val origin: PluginOrigin,
)