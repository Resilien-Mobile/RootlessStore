package com.baidaidai.rootless_store.domain.codebrick.model

import com.baidaidai.rootless_store.domain.status.model.ExecutionContext

data class CodeBrickConfig(
    val unixTimestamp: Long,
    val codeBrickTitle: String,
    val codeBrickEnvironment: ExecutionContext,
    val codeBrickContent: String,
    val boundTileIndex: Int? = null
)