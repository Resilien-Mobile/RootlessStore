package com.baidaidai.rootless_store.data.update.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubReleaseDto(
    @SerialName("tag_name")
    val tagName: String
)
