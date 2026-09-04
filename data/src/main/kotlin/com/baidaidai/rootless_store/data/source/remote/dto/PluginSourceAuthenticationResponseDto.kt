package com.baidaidai.rootless_store.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PluginSourceAuthenticationResponseDto(
    @SerialName("userName")
    val username: String,
    @SerialName("userAccessToken")
    val accessToken: String
)
