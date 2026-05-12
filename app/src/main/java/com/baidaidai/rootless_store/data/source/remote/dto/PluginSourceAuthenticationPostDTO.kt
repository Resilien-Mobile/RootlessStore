package com.baidaidai.rootless_store.data.source.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PluginSourceAuthenticationPostDTO(
    val userName: String,
    val passWord: String
)
