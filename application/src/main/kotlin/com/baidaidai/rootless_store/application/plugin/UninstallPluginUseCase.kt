package com.baidaidai.rootless_store.application.plugin

import com.baidaidai.rootless_store.data.plugin.gateway.PluginGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginStatusRepositoryImpl
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import javax.inject.Inject

class UninstallPluginUseCase @Inject constructor(
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val pluginStatusRepositoryImpl: PluginStatusRepositoryImpl,
    private val pluginFileSystemGateway: PluginGatewayImpl,
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl,
) {
    suspend operator fun invoke(
        pluginManifest: PluginManifest
    ){
        if (pluginManifest.requiredEnvironment == ExecutionContext.ADB){
            uninstallShellPlugin(pluginManifest) ; return
        }
        pluginFileSystemGateway.uninstallPlugin(pluginManifest.pluginPackageName)
        deletePlugin(pluginManifest.pluginId)
    }

    private suspend fun uninstallShellPlugin(
        pluginManifest: PluginManifest
    ){
        val isShellPluginUninstallSuccessful = shizukuUserServiceGatewayImpl
            .findShizukuUserService()
            ?.uninstallShellPlugin(pluginManifest.pluginPackageName) ?: false

        if (!isShellPluginUninstallSuccessful) return

        deletePlugin(pluginManifest.pluginId)
    }

    private suspend fun deletePlugin(pluginId: String) {
        pluginRepositoryImpl.deletePluginById(pluginId)
        pluginStatusRepositoryImpl.deletePluginStatus(pluginId)
    }
}
