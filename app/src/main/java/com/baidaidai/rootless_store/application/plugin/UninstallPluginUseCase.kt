package com.baidaidai.rootless_store.application.plugin

import com.baidaidai.rootless_store.data.plugin.gateway.PluginGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import javax.inject.Inject

class UninstallPluginUseCase @Inject constructor(
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val pluginFileSystemGateway: PluginGatewayImpl,
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl,
) {
    suspend operator fun invoke(
        pluginManifestRoom: PluginManifestRoom
    ){
        if (pluginManifestRoom.requiredEnvironment == HosterOverallStatus.ADB){
            uninstallShellPlugin(pluginManifestRoom) ; return
        }
        pluginFileSystemGateway.uninstallPlugin(pluginManifestRoom.pluginPackageName)
        pluginRepositoryImpl.deletePluginById(pluginManifestRoom.pluginId)
    }
    private suspend fun uninstallShellPlugin(
        pluginManifestRoom: PluginManifestRoom
    ){
        val shellPluginUninstallResult = shizukuUserServiceGatewayImpl
            .findShizukuUserService()
            ?.uninstallShellPlugin(pluginManifestRoom.pluginPackageName) ?: false

        if (!shellPluginUninstallResult) return

        pluginRepositoryImpl.deletePluginById(pluginManifestRoom.pluginId)
    }
}
