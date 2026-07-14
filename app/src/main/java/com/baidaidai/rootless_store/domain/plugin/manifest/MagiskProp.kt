package com.baidaidai.rootless_store.domain.plugin.manifest

import kotlinx.serialization.Serializable

@Serializable
data class MagiskProp(
    val id: String,
    val name: String,
    val version: String,
    val author: String,
    val description: String
)