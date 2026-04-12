package com.baidaidai.rootless_store.domain.shell.model

data class ShellCommandContainer(
    val shellEnvironment: ShellEnvironment,
    val commandContent: String
)
