package com.baidaidai.rootless_store.domain.shell.model

data class AppShellContextConfig(
    val jumpToDirectory: Boolean,
    override val environmentPath: String,
    override val environmentLdPath: String,
    override val environmentConfig: Map<String, String>
): ShellContextConfig
