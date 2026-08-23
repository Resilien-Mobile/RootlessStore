package com.baidaidai.rootless_store.domain.execution.model

import javax.inject.Inject

data class ExecutionResult @Inject constructor(
    val resultTag: ExecutionResultTag,
    val content: String
)
