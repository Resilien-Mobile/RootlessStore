package com.baidaidai.rootless_store.data.plugin.mapper

import com.baidaidai.rootless_store.data.plugin.database.PluginEntity
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestLocal
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRemote
import com.baidaidai.rootless_store.domain.plugin.model.PluginSource
import com.baidaidai.rootless_store.domain.plugin.model.PluginState

object PluginMapper {

    fun PluginManifestLocal.toPluginEntity(): PluginEntity {
        return PluginEntity(
            pluginId = pluginId,
            installedVersion = installedVersion,
            pluginRenderingName = pluginRenderingName,
            pluginPackageName = pluginPackageName,
            iconUri = iconUri,
            author = author,
            pluginDescription = pluginDescription,
            isEnabled = false,
            requiredEnvironment = requiredEnvironment,
            state = PluginState.Great,
            source = PluginSource.Local,
            entryPoint = entryPoint,
            pluginRunModel = pluginRunModel,
            webUiEntryPoint = webUiEntryPoint
        )
    }
    fun PluginManifestRemote.toPluginEntity(): PluginEntity {
        return PluginEntity(
            pluginId = pluginId,
            installedVersion = installedVersion,
            pluginRenderingName = pluginRenderingName,
            pluginPackageName = pluginPackageName,
            iconUri = iconUri,
            author = author,
            pluginDescription = pluginDescription,
            isEnabled = false,
            requiredEnvironment = requiredEnvironment,
            state = PluginState.Great,
            source = PluginSource.Official,
            entryPoint = entryPoint,
            pluginRunModel = pluginRunModel,
            webUiEntryPoint = webUiEntryPoint,
        )
    }

}
