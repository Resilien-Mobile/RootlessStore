package com.baidaidai.rootless_store.data.update.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GithubReleaseDTO(
    val tag_name: String
)