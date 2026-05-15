package com.baidaidai.rootless_store.domain.plugin.usecase

import com.baidaidai.rootless_store.data.plugin.gateway.PluginCoreGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginCoreRepositoryImpl
import com.baidaidai.rootless_store.data.plugin.room.PluginInfoEntity
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import javax.inject.Inject

class UninstallOnePluginUseCase @Inject constructor(
    private val repositoryImpl: PluginCoreRepositoryImpl,
    private val pluginFileSystemGateway: PluginCoreGatewayImpl,
) {
    suspend operator fun invoke(
        pluginManifestRoom: PluginManifestRoom
    ){
        val pluginInfoEntity = PluginInfoEntity.fromPluginManifestRoom(pluginManifestRoom)
        val pluginPackageName = pluginInfoEntity.pluginPackageName

        pluginFileSystemGateway.uninstallPlugin(pluginPackageName)
        repositoryImpl.deleteOnePluginInfo(pluginInfoEntity)
    }
}