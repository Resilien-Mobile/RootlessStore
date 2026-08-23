package com.baidaidai.rootless_store.application.plugin

import com.baidaidai.rootless_store.data.plugin.gateway.PluginGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRemote
import javax.inject.Inject

class InstallPluginFromMarketUseCase @Inject constructor(
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val pluginGatewayImpl: PluginGatewayImpl
) {
    suspend operator fun invoke(
        pluginUri: String,
        pluginManifestRemote: PluginManifestRemote
    ) {
        pluginGatewayImpl.installPluginFromMarket(pluginUri,pluginManifestRemote)
        pluginGatewayImpl.setPluginEntryPointExecutable(pluginManifestRemote)
        pluginRepositoryImpl.insertPlugin(pluginManifestRemote)

    }
}
