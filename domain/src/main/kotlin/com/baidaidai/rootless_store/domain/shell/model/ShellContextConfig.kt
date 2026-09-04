package com.baidaidai.rootless_store.domain.shell.model

interface ShellContextConfig {

    val environmentPath: String
    val environmentLdPath: String
    val environmentConfig: Map<String, String>

}