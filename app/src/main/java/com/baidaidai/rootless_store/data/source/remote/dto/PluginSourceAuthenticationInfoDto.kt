package com.baidaidai.rootless_store.data.source.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PluginSourceAuthenticationInfoDto(
    val userName: String,
    val userAccessToken: String
)
