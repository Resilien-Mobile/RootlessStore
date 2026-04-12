package com.baidaidai.rootless_store.domain.shell.model

import com.baidaidai.rootless_store.domain.execute.model.ResultTag
import kotlin.String

data class ShellResult(
    val resulTag: ResultTag,
    val command: String,
    val content: String
)
