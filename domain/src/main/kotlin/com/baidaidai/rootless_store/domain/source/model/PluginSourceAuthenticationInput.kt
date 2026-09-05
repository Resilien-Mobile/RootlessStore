package com.baidaidai.rootless_store.domain.source.model

import kotlinx.serialization.Serializable

@Serializable
data class PluginSourceAuthenticationInput(
    val sourceRemoteEndpoint: String,
    val username: String,
    val password: String
)
