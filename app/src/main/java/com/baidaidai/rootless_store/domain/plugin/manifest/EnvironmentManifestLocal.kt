package com.baidaidai.rootless_store.domain.plugin.manifest

import com.baidaidai.rootless_store.domain.plugin.model.PluginSource
import com.baidaidai.rootless_store.domain.plugin.model.PluginState
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import kotlinx.serialization.Serializable

@Serializable
data class EnvironmentManifestLocal(
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
): EnvironmentManifest.EnvironmentManifestLocal{
    companion object {
        val _testOnly_ = EnvironmentManifestLocal(
            installedVersion = "x.x.x",
            environmentRenderingName=  "Test Environment",
            environmentPackageName = "TestEnvironment",
            environmentID = "29bb10c46772264df3c0d0fade57d2eb",
            iconURI = "content://rootless_store/environment_icon/test",
            author = "Rootless Store(Creater. Bai)",
            requiredEnvironment = HosterOverallStatus.LIMITED,
            environmentDescription = "Tested by Creater. Bai",
            entryPoint = "./index.sh",
            ldLibraryPath = emptyList(),
            env = emptyMap()
        )
    }
    fun toManifestRoom(): EnvironmentManifestRoom{
        return EnvironmentManifestRoom(
            enabled = false,
            state = PluginState.Great,
            source = PluginSource.Local,
            installedVersion = installedVersion,
            environmentRenderingName = environmentRenderingName,
            environmentPackageName = environmentPackageName,
            environmentID = environmentID,
            iconURI = iconURI,
            author = author,
            environmentDescription = environmentDescription,
            requiredEnvironment = requiredEnvironment,
            entryPoint = entryPoint,
            ldLibraryPath = ldLibraryPath,
            env = env
        )
    }
}