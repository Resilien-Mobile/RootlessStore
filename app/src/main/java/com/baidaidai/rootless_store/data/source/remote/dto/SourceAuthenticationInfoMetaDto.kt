package com.baidaidai.rootless_store.data.source.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SourceAuthenticationInfoMetaDto(
    val requireAuthentication: Boolean
)