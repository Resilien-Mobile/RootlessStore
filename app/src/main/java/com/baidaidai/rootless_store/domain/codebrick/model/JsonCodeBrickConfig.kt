package com.baidaidai.rootless_store.domain.codebrick.model

import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import kotlinx.serialization.Serializable

@Serializable
data class JsonCodeBrickConfig(
    val codeBrickTitle: String,
    val codeBrickEnvironment: HosterOverallStatus,
    val codeBrickContent: String
)