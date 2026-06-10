package com.baidaidai.rootless_store.domain.shell.model

interface ShellContextConfig {

    val environmentPATH: String
    val environmentLDPATH: String
    val environmentConfig: Map<String, String>

}