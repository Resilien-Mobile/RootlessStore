package com.baidaidai.rootless_store.domain.plugin.manifest

import com.baidaidai.rootless_store.domain.plugin.model.PluginRunModel
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("PluginManifestRemote")
data class PluginManifestRemote(
    override val installedVersion: String,
    override val pluginRenderingName: String,
    override val pluginPackageName: String,
    @SerialName("pluginID")
    override val pluginId: String,
    @SerialName("iconURI")
    override val iconUri: String?,
    override val author: String,
    override val pluginDescription: String,
    override val requiredEnvironment: HosterOverallStatus,
    @SerialName("pluginURI")
    override val pluginUri: String,
    override val entryPoint: String,

    override val pluginRunModel: PluginRunModel,
    @SerialName("webUIEntryPoint")
    override val webUiEntryPoint: String? = null
): PluginManifest.PluginManifestRemote {
    companion object {
        val _testOnly_ = PluginManifestRemote(
            installedVersion = "x.x.x",
            pluginRenderingName = "Test Plugin",
            pluginPackageName = "TestPlugin",
            pluginId = "29bb10c46772264df3c0d0fade57d2eb",
            pluginUri = "http://test.only.ai/api/v3/assets/plugin?id=29bb10c46772264df3c0d0fade57d2eb",
            iconUri = "content://rootless_store/plugin_icon/test",
            author = "Rootless Store(Creater. Bai)",
            requiredEnvironment = HosterOverallStatus.ADB,
            pluginDescription = "Tested by Creater.",
            entryPoint = "./index.sh",
            pluginRunModel = PluginRunModel.OneTime
        )
    }
}
