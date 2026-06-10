package com.baidaidai.rootless_store.domain.shell.model

data class AdbShellContextConfig(
    val useRunAs: Boolean,
    override val environmentPATH: String,
    override val environmentLDPATH: String,
    override val environmentConfig: Map<String, String>,
    val environmentConfigKeyList: List<String>,
    val environmentConfigValueList: List<String>
): ShellContextConfig
