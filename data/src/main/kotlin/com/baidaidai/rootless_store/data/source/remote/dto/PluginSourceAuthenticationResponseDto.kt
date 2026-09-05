package com.baidaidai.rootless_store.data.source.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PluginSourceAuthenticationResponseDto(
    val username: String,
    val accessToken: String
)
