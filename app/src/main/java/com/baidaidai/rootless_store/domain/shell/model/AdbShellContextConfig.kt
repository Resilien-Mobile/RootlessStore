package com.baidaidai.rootless_store.domain.shell.model

data class AdbShellContextConfig(
    val shouldJumpToDirectory: Boolean,
    override val environmentPath: String,
    override val environmentLdPath: String,
    override val environmentConfig: Map<String, String>,
    val environmentConfigKeys: List<String>,
    val environmentConfigValues: List<String>
): ShellContextConfig
