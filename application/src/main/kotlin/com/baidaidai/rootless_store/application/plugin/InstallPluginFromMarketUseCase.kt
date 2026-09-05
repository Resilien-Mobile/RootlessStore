package com.baidaidai.rootless_store.application.plugin

import com.baidaidai.rootless_store.data.plugin.gateway.PluginGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import com.baidaidai.rootless_store.domain.plugin.model.PluginOrigin
import javax.inject.Inject

class InstallPluginFromMarketUseCase @Inject constructor(
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val pluginStatusRepositoryImpl: PluginStatusRepositoryImpl,
    private val pluginGatewayImpl: PluginGatewayImpl
) {
    suspend operator fun invoke(
        pluginUrl: String,
        pluginManifest: PluginManifest
    ) {
        pluginGatewayImpl.installPluginFromMarket(pluginUrl, pluginManifest)
        pluginGatewayImpl.setPluginEntryPointExecutable(pluginManifest)
        pluginRepositoryImpl.addPlugin(pluginManifest)
        pluginStatusRepositoryImpl.registerPluginStatus(pluginManifest.pluginId, PluginOrigin.Official)
    }
}
