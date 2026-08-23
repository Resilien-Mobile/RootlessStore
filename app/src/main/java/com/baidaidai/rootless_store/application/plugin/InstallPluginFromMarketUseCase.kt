package com.baidaidai.rootless_store.application.plugin

import com.baidaidai.rootless_store.data.plugin.gateway.PluginGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.domain.module.model.ModuleManifestCollection
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRemote
import javax.inject.Inject

class InstallPluginFromMarketUseCase @Inject constructor(
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val pluginGatewayImpl: PluginGatewayImpl
) {
    suspend operator fun invoke(
        pluginUri: String,
        manifest: ModuleManifestCollection
    ) {
        val pluginManifestRemote = manifest as PluginManifestRemote

        pluginGatewayImpl.installPluginFromMarket(pluginUri,pluginManifestRemote)
        pluginGatewayImpl.setPluginEntryPointExecutable(manifest)
        pluginRepositoryImpl.insertPlugin(pluginManifestRemote)

    }
}
