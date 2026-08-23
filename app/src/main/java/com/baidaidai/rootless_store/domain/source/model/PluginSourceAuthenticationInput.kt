package com.baidaidai.rootless_store.domain.source.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PluginSourceAuthenticationInput(
    val sourceRemoteEndpoint: String,
    @SerialName("userName")
    val username: String,
    @SerialName("passWord")
    val password: String
)
