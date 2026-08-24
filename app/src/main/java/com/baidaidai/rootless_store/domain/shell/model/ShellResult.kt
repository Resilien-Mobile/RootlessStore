package com.baidaidai.rootless_store.domain.shell.model

import com.baidaidai.rootless_store.domain.execution.model.ExecutionResultTag
import kotlin.String

data class ShellResult(
    val resultTag: ExecutionResultTag,
    val command: String?,
    val output: String
)
