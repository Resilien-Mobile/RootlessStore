package com.baidaidai.rootless_store.domain.codebrick.model

import com.baidaidai.rootless_store.domain.status.model.ExecutionContext

data class CodeBrickContextConfig(
    val contextType: ExecutionContext,
    val contextTextResource: Int,

    val contextIcon: Int
)
