package com.baidaidai.rootless_store.domain.source.model

import kotlinx.serialization.Serializable

@Serializable
data class PluginSourceEndpointInput(
    val sourceRemoteEndpoint: String
)
