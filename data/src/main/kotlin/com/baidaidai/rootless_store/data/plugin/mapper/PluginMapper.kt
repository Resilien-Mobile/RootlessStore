package com.baidaidai.rootless_store.data.plugin.mapper

import com.baidaidai.rootless_store.data.plugin.database.PluginEntity
import com.baidaidai.rootless_store.data.plugin.database.PluginStatusEntity
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import com.baidaidai.rootless_store.domain.plugin.model.PluginStatus

object PluginMapper {

    fun PluginManifest.toPluginEntity(): PluginEntity {
        return PluginEntity(
            pluginId = pluginId,
            installedVersion = installedVersion,
            pluginRenderingName = pluginRenderingName,
            pluginPackageName = pluginPackageName,
            iconUri = iconUri,
            author = author,
            pluginDescription = pluginDescription,
            requiredEnvironment = requiredEnvironment,
            entryPoint = entryPoint,
            pluginRunModel = pluginRunModel,
            webUiEntryPoint = webUiEntryPoint
        )
    }

    fun PluginEntity.toPluginManifest(): PluginManifest {
        return PluginManifest(
            installedVersion = installedVersion,
            pluginRenderingName = pluginRenderingName,
            pluginPackageName = pluginPackageName,
            pluginId = pluginId,
            iconUri = iconUri,
            author = author,
            pluginDescription = pluginDescription,
            requiredEnvironment = requiredEnvironment,
            pluginRunModel = pluginRunModel,
            entryPoint = entryPoint,
            webUiEntryPoint = webUiEntryPoint
            // executableFiles / pluginUrl are not persisted
        )
    }

    fun PluginStatusEntity.toPluginStatus(): PluginStatus {
        return PluginStatus(
            pluginId = pluginId,
            isEnabled = isEnabled,
            state = state,
            origin = origin
        )
    }

    fun PluginStatus.toPluginStatusEntity(): PluginStatusEntity {
        return PluginStatusEntity(
            pluginId = pluginId,
            isEnabled = isEnabled,
            state = state,
            origin = origin
        )
    }
}
