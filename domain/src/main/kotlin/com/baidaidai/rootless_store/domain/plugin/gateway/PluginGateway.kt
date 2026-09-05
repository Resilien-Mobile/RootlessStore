package com.baidaidai.rootless_store.domain.plugin.gateway

import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest

interface PluginGateway {
    suspend fun installPluginFromMarket(pluginUrl: String, pluginManifest: PluginManifest)
    fun uninstallPlugin(pluginPackageName: String)
}
