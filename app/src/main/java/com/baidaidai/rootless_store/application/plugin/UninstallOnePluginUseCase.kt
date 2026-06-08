package com.baidaidai.rootless_store.application.plugin

import com.baidaidai.rootless_store.data.plugin.gateway.PluginGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import javax.inject.Inject

class UninstallOnePluginUseCase @Inject constructor(
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val pluginFileSystemGateway: PluginGatewayImpl,
) {
    suspend operator fun invoke(
        pluginManifestRoom: PluginManifestRoom
    ){
        pluginFileSystemGateway.uninstallPlugin(pluginManifestRoom.pluginPackageName)
        pluginRepositoryImpl.deleteOnePluginInfoByID(pluginManifestRoom.pluginID)
    }
}