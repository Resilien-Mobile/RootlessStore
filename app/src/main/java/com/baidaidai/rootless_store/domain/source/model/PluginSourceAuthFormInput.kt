package com.baidaidai.rootless_store.domain.source.model

import kotlinx.serialization.Serializable

@Serializable
data class PluginSourceAuthFormInput(
    val sourceRemoteEndpoint: String,
    val userName: String,
    val passWord: String
)
