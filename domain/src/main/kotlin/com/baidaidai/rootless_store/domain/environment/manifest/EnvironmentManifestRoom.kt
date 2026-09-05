package com.baidaidai.rootless_store.domain.environment.manifest

import com.baidaidai.rootless_store.domain.plugin.model.PluginOrigin
import com.baidaidai.rootless_store.domain.plugin.model.PluginState
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext

data class EnvironmentManifestRoom(
    override val isEnabled: Boolean,
    override val state: PluginState,
    override val origin: PluginOrigin,
    override val installedVersion: String,
    override val environmentRenderingName: String,
    override val environmentPackageName: String,
    override val environmentId: String,
    override val iconUri: String?,
    override val author: String,
    override val environmentDescription: String,
    override val requiredEnvironment: ExecutionContext,
    override val entryPoint: String,
    override val ldLibraryPath: List<String>,
    override val env: Map<String, String>
): EnvironmentManifest.EnvironmentManifestRoom{

    companion object {
        val _testOnly_ = EnvironmentManifestRoom(
            installedVersion = "x.x.x",
            environmentRenderingName = "Test Environment",
            environmentPackageName = "TestEnvironment",
            environmentId = "29bb10c46772264df3c0d0fade57d2eb",
            iconUri = "content://rootless_store/environment_icon/test",
            author = "Rootless Store(Creater. Bai)",
            requiredEnvironment = ExecutionContext.LIMITED,
            environmentDescription = "Tested by Creater. Bai",
            isEnabled = false,
            state = PluginState.PermissionProblems,
            origin = PluginOrigin.Local,
            entryPoint = "./index.sh",
            ldLibraryPath = emptyList(),
            env = emptyMap()
        )
    }

}
