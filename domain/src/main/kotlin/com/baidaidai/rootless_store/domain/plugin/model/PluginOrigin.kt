package com.baidaidai.rootless_store.domain.plugin.model

import kotlinx.serialization.Serializable

@Serializable
enum class PluginOrigin {
    Official, Third, Local
}
