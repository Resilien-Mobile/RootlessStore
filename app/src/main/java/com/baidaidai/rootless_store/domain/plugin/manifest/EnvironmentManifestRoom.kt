package com.baidaidai.rootless_store.domain.plugin.manifest

import com.baidaidai.rootless_store.domain.plugin.model.PluginSource
import com.baidaidai.rootless_store.domain.plugin.model.PluginState
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus

data class EnvironmentManifestRoom(
    override val enabled: Boolean,
    override val state: PluginState,
    override val source: PluginSource,
    override val installedVersion: String,
    override val environmentRenderingName: String,
    override val environmentPackageName: String,
    override val environmentID: String,
    override val iconURI: String?,
    override val author: String,
    override val environmentDescription: String,
    override val requiredEnvironment: HosterOverallStatus,
    override val entryPoint: String,
    override val ldLibraryPath: List<String>,
    override val env: Map<String, String>
): EnvironmentManifest.EnvironmentManifestRoom{
    companion object {
        val _testOnly_ = EnvironmentManifestRoom(
            installedVersion = "x.x.x",
            environmentRenderingName = "Test Environment",
            environmentPackageName = "TestEnvironment",
            environmentID = "29bb10c46772264df3c0d0fade57d2eb",
            iconURI = "content://rootless_store/environment_icon/test",
            author = "Rootless Store(Creater. Bai)",
            requiredEnvironment = HosterOverallStatus.LIMITED,
            environmentDescription = "Tested by Creater. Bai",
            enabled = false,
            state = PluginState.PermissionProblems,
            source = PluginSource.Local,
            entryPoint = "./index.sh",
            ldLibraryPath = emptyList(),
            env = emptyMap()
        )
    }
}