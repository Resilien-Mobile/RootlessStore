package com.baidaidai.rootless_store.domain.plugin.gateway

import android.net.Uri
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRemote

interface PluginGateway {
    fun installPluginFromLocal(originFileUri: Uri): Unit
    suspend fun installPluginFromMarket(pluginUrl: String, pluginManifestRemote: PluginManifestRemote)
    fun uninstallPlugin(pluginPackageName: String): Unit
}
