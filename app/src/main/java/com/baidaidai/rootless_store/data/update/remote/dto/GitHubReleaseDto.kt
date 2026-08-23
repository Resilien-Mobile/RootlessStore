package com.baidaidai.rootless_store.data.update.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GitHubReleaseDto(
    val tag_name: String
)