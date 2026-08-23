package com.baidaidai.rootless_store.domain.plugin.manifest

import androidx.room.ColumnInfo
import com.baidaidai.rootless_store.domain.plugin.model.PluginRunModel
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
    @ColumnInfo(name = "pluginID")
    override val pluginId: String,
    @ColumnInfo(name = "iconURI")
    override val iconUri: String?,
    override val author: String,
    override val pluginDescription: String,
    override val requiredEnvironment: HosterOverallStatus,
    override val entryPoint: String,
    override val pluginRunModel: PluginRunModel,
    @ColumnInfo(name = "webUIEntryPoint")
    override val webUiEntryPoint: String? = null
): PluginManifest.PluginManifestRoom{

    companion object {
        val _testOnly_ = PluginManifestRoom(
            installedVersion = "x.x.x",
            pluginRenderingName = "Test Plugin",
            pluginPackageName = "TestPlugin",
            pluginId = "29bb10c46772264df3c0d0fade57d2eb",
            iconUri = null,
            author = "Rootless Store(Creater. Bai)",
            requiredEnvironment = HosterOverallStatus.LIMITED,
            pluginDescription = "Tested by Creater. Bai",
            enabled = false,
            state = PluginState.PermissionProblems,
            source = PluginSource.Local,
            entryPoint = "./index.sh",
            pluginRunModel = PluginRunModel.OneTime
        )
    }
}
