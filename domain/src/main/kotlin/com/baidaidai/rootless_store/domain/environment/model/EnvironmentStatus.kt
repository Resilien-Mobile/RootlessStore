package com.baidaidai.rootless_store.domain.environment.model

import com.baidaidai.rootless_store.domain.plugin.model.PluginOrigin
import com.baidaidai.rootless_store.domain.plugin.model.PluginState

data class EnvironmentStatus(
    val environmentId: String,
    val isEnabled: Boolean,
    val state: PluginState,
    val origin: PluginOrigin,
)
