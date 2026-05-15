package com.baidaidai.rootless_store.domain.plugin.manifest

import com.baidaidai.rootless_store.domain.plugin.model.PluginRunModel
import com.baidaidai.rootless_store.domain.plugin.model.PluginSource
import com.baidaidai.rootless_store.domain.plugin.model.PluginState
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("PluginManifestRemote")
data class PluginManifestRemote(
    override val installedVersion: String,
    override val pluginRenderingName: String,
    override val pluginPackageName: String,
    override val pluginID: String,
    override val iconURI: String?,
    override val author: String,
    override val pluginDescription: String,
    override val requiredEnvironment: HosterOverallStatus,
    override val pluginURI: String,
    override val entryPoint: String,

    override val pluginRunModel: PluginRunModel
): PluginManifest.PluginManifestRemote {
    companion object {
        val _testOnly_ = PluginManifestRemote(
            installedVersion = "x.x.x",
            pluginRenderingName = "Test Plugin",
            pluginPackageName = "TestPlugin",
            pluginID = "29bb10c46772264df3c0d0fade57d2eb",
            pluginURI = "http://test.only.ai/api/v3/assets/plugin?id=29bb10c46772264df3c0d0fade57d2eb",
            iconURI = "content://rootless_store/plugin_icon/test",
            author = "Rootless Store(Creater. Bai)",
            requiredEnvironment = HosterOverallStatus.ADB,
            pluginDescription = "Tested by Creater.",
            entryPoint = "./index.sh",
            pluginRunModel = PluginRunModel.OneTime
        )
    }
    fun toManifestRoom(): PluginManifestRoom{
        return PluginManifestRoom(
            enabled = false,
            state = PluginState.Great,
            source = PluginSource.Official,
            installedVersion = installedVersion,
            pluginRenderingName = pluginRenderingName,
            pluginPackageName = pluginPackageName,
            pluginID = pluginID,
            iconURI = iconURI,
            author = author,
            pluginDescription = pluginDescription,
            requiredEnvironment = requiredEnvironment,
            entryPoint = entryPoint
        )
    }
}
