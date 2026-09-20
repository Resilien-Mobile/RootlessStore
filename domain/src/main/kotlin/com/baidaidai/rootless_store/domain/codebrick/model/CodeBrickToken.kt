package com.baidaidai.rootless_store.domain.codebrick.model

import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import kotlinx.serialization.Serializable

/**
 * 用于区别与 Room 持久层的 CodeBrickConfig
 * CodeBrickToken 带可序列化标记，但是不带唯一主键
 */
@Serializable
data class CodeBrickToken(
    val codeBrickTitle: String,
    val codeBrickEnvironment: ExecutionContext,
    val codeBrickContent: String
)
