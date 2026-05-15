package com.baidaidai.rootless_store.domain.plugin.usecase

import com.baidaidai.rootless_store.data.plugin.gateway.PluginCoreGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginCoreRepositoryImpl
import com.baidaidai.rootless_store.data.plugin.room.EnvironmentInfoEntity
import com.baidaidai.rootless_store.domain.plugin.manifest.EnvironmentManifestRoom
import javax.inject.Inject

class UninstallOneEnvironmentUseCase @Inject constructor(
    private val repositoryImpl: PluginCoreRepositoryImpl,
    private val pluginFileSystemGateway: PluginCoreGatewayImpl,
) {
    suspend operator fun invoke(
        environmentManifestRoom: EnvironmentManifestRoom
    ){
        val environmentInfoEntity = EnvironmentInfoEntity.fromEnvironmentManifestRoom(environmentManifestRoom)
        val environmentPackageName = environmentInfoEntity.environmentPackageName

        pluginFileSystemGateway.uninstallEnvironment(environmentPackageName)
        repositoryImpl.deleteOneEnvironmentInfo(environmentInfoEntity)
    }
}