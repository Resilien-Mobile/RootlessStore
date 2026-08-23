package com.baidaidai.rootless_store.domain.plugin.manifest

import com.baidaidai.rootless_store.domain.plugin.model.PluginRunModel
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Plugin manifest / metadata model.
 *
 * ⚠️ Status: **initial version only**
 *
 * - Some types/fields must still be confirmed manually in this stage.
 * - In later versions, these parameters will be loaded from a JSON manifest
 *   (via deserialization).
 * - The `0.x.x` series is **data-only** for now; do not rely on stable APIs yet.
 *
 * Conventions:
 * - Keep this class focused on **static plugin metadata**.
 * - Runtime state (enabled/state/source, etc.) should live in a separate model
 *   (e.g., DB/runtime entity), not inside this manifest.
 */
@Serializable
data class PluginManifestLocal(
    override val installedVersion: String,
    override val pluginRenderingName: String,
    override val pluginPackageName: String,
    @SerialName("pluginID")
    override val pluginId: String,
    @SerialName("iconURI")
    override val iconUri: String?,
    override val author: String,
    override val pluginDescription: String,
    override val requiredEnvironment: ExecutionContext,
    override val entryPoint: String,
    override val pluginRunModel: PluginRunModel,
    @SerialName("webUIEntryPoint")
    override val webUiEntryPoint: String? = null,
    override val executableFiles: List<String>? = null
): PluginManifest.PluginManifestLocal{
    companion object {
        val _testOnly_ = PluginManifestLocal(
            installedVersion = "x.x.x",
            pluginRenderingName=  "Test Plugin",
            pluginPackageName = "TestPlugin",
            pluginId = "29bb10c46772264df3c0d0fade57d2eb",
            iconUri = "content://rootless_store/plugin_icon/test",
            author = "Rootless Store(Creater. Bai)",
            requiredEnvironment = ExecutionContext.LIMITED,
            pluginDescription = "Tested by Creater. Bai",
            entryPoint = "./index.sh",
            pluginRunModel = PluginRunModel.OneTime
        )
    }
}
