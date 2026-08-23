package com.baidaidai.rootless_store.domain.codebrick.model

import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import kotlinx.serialization.Serializable

@Serializable
data class CodeBrickJsonPayload(
    val codeBrickTitle: String,
    val codeBrickEnvironment: ExecutionContext,
    val codeBrickContent: String
)
