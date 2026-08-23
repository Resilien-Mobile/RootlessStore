package com.baidaidai.rootless_store.domain.environment.manifest

import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EnvironmentManifestLocal(
    override val installedVersion: String,
    override val environmentRenderingName: String,
    override val environmentPackageName: String,
    @SerialName("environmentID")
    override val environmentId: String,
    @SerialName("iconURI")
    override val iconUri: String?,
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
            environmentId = "29bb10c46772264df3c0d0fade57d2eb",
            iconUri = "content://rootless_store/environment_icon/test",
            author = "Rootless Store(Creater. Bai)",
            requiredEnvironment = HosterOverallStatus.LIMITED,
            environmentDescription = "Tested by Creater. Bai",
            entryPoint = "./index.sh",
            ldLibraryPath = emptyList(),
            env = emptyMap()
        )
    }
}
