package com.baidaidai.rootless_store.domain.plugin.gateway

import android.net.Uri
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRemote

interface PluginCoreGateway {
    fun installPluginFromLocal(originFileURI: Uri): Unit
    suspend fun installPluginFromMarket(pluginURI: String, pluginManifestRemote: PluginManifestRemote)
    fun uninstallPlugin(pluginPackageName: String): Unit
}