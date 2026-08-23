package com.baidaidai.rootless_store.domain.shell.model

data class ShellCommand(
    val shellEnvironment: ShellEnvironment,
    val commandContent: String
)
