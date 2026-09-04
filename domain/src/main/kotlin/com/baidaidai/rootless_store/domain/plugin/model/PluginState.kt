package com.baidaidai.rootless_store.domain.plugin.model

import kotlinx.serialization.Serializable

@Serializable
enum class PluginState {
    Great,PermissionProblems,PluginRuntimeProblems,RootlessStoreRuntimeProblems,Stop
}