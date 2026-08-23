package com.baidaidai.rootless_store.domain.plugin.gateway

import android.net.Uri
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRemote

interface PluginCoreGateway {
    fun installPluginFromLocal(originFileUri: Uri): Unit
    suspend fun installPluginFromMarket(pluginUri: String, pluginManifestRemote: PluginManifestRemote)
    fun uninstallPlugin(pluginPackageName: String): Unit
}