package com.baidaidai.rootless_store.domain.plugin.manifest

import com.baidaidai.rootless_store.domain.plugin.model.PluginSource
import com.baidaidai.rootless_store.domain.plugin.model.PluginState
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus

data class PluginManifestRoom(
    override val enabled: Boolean,
    override val state: PluginState,
    override val source: PluginSource,
    override val installedVersion: String,
    override val pluginRenderingName: String,
    override val pluginPackageName: String,
    override val pluginID: String,
    override val iconURI: String?,
    override val author: String,
    override val pluginDescription: String,
    override val requiredEnvironment: HosterOverallStatus,
    override val entryPoint: String
): PluginManifest.PluginManifestRoom{
    companion object {
        val _testOnly_ = PluginManifestRoom(
            installedVersion = "x.x.x",
            pluginRenderingName = "Test Plugin",
            pluginPackageName = "TestPlugin",
            pluginID = "29bb10c46772264df3c0d0fade57d2eb",
            iconURI = null,
            author = "Rootless Store(Creater. Bai)",
            requiredEnvironment = HosterOverallStatus.LIMITED,
            pluginDescription = "Tested by Creater. Bai",
            enabled = false,
            state = PluginState.PermissionProblems,
            source = PluginSource.Local,
            entryPoint = "./index.sh"
        )
    }
}
