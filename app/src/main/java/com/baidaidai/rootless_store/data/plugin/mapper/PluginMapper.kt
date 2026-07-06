package com.baidaidai.rootless_store.data.plugin.mapper

import com.baidaidai.rootless_store.data.plugin.database.PluginInfoEntity
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestLocal
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRemote
import com.baidaidai.rootless_store.domain.plugin.model.PluginSource
import com.baidaidai.rootless_store.domain.plugin.model.PluginState

object PluginMapper {

    fun PluginManifestLocal.toPluginInfoEntity(): PluginInfoEntity {
        return PluginInfoEntity(
            pluginID = pluginID,
            installedVersion = installedVersion,
            pluginRenderingName = pluginRenderingName,
            pluginPackageName = pluginPackageName,
            iconURI = iconURI,
            author = author,
            pluginDescription = pluginDescription,
            enabled = false,
            requiredEnvironment = requiredEnvironment,
            state = PluginState.Great,
            source = PluginSource.Local,
            entryPoint = entryPoint,
            pluginRunModel = pluginRunModel,
            webUIEntryPoint = webUIEntryPoint
        )
    }
    fun PluginManifestRemote.toPluginInfoEntity(): PluginInfoEntity {
        return PluginInfoEntity(
            pluginID = pluginID,
            installedVersion = installedVersion,
            pluginRenderingName = pluginRenderingName,
            pluginPackageName = pluginPackageName,
            iconURI = iconURI,
            author = author,
            pluginDescription = pluginDescription,
            enabled = false,
            requiredEnvironment = requiredEnvironment,
            state = PluginState.Great,
            source = PluginSource.Official,
            entryPoint = entryPoint,
            pluginRunModel = pluginRunModel,
            webUIEntryPoint = webUIEntryPoint,
        )
    }

}
