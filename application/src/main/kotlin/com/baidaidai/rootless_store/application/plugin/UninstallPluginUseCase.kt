package com.baidaidai.rootless_store.application.plugin

import android.content.Context
import com.baidaidai.rootless_store.application.R
import com.baidaidai.rootless_store.core.util.formatAsMultilineString
import com.baidaidai.rootless_store.data.plugin.gateway.PluginGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginStatusRepositoryImpl
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuPermissionGatewayImpl
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UninstallPluginUseCase @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val pluginStatusRepositoryImpl: PluginStatusRepositoryImpl,
    private val pluginFileSystemGateway: PluginGatewayImpl,
    private val shizukuPermissionGatewayImpl: ShizukuPermissionGatewayImpl,
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl,
) {
    suspend operator fun invoke(
        pluginManifest: PluginManifest
    ): Result<Unit, PluginError> {

        if (pluginManifest.requiredEnvironment == ExecutionContext.ADB) {
            return uninstallAdbPlugin(pluginManifest)
        }

        val appPluginUninstallError = pluginFileSystemGateway
            .uninstallPlugin(pluginManifest.pluginPackageName)
            .exceptionOrNull()

        if (appPluginUninstallError != null) {
            return Err(
                PluginError(
                    errorMessage = "Can't uninstall plugin",
                    errorCause = appPluginUninstallError.stackTrace.formatAsMultilineString(),
                    errorCompanion = "Rootless Store could not remove the plugin files from app storage."
                )
            )
        }

        return deletePluginRecords(pluginManifest.pluginId)

    }

    private suspend fun uninstallAdbPlugin(
        pluginManifest: PluginManifest
    ): Result<Unit, PluginError> {
        // 权限检查：确认当前进程具备调用 Shizuku 的权限。
        if (!shizukuPermissionGatewayImpl.canUseShizuku()) {
            return Err(
                PluginError(
                    errorMessage = "Oops！",
                    errorCompanion = context.getString(
                        R.string.uninstall_plugin_shizuku_permission_required
                    )
                )
            )
        }

        val useRedundancyUninstall = pluginManifest.webUiEntryPoint != null
        if (useRedundancyUninstall) {

            // 本地副本清理：WebUI 插件在应用存储中有副本，需要与 Shell 侧文件一并卸载。
            val appPluginUninstallError = pluginFileSystemGateway
                .uninstallPlugin(pluginManifest.pluginPackageName)
                .exceptionOrNull()

            if (appPluginUninstallError != null) {
                return Err(
                    PluginError(
                        errorMessage = "Can't uninstall plugin copy",
                        errorCause = appPluginUninstallError.stackTrace.formatAsMultilineString(),
                        errorCompanion = "The WebUI plugin copy in app storage could not be removed."
                    )
                )
            }

        }

        // 服务获取：权限可用不代表 Shizuku UserService 已连接，单独检查服务是否可达。
        val shizukuUserService = shizukuUserServiceGatewayImpl.findShizukuUserService()
            ?: return Err(
                PluginError(
                    errorMessage = "Shizuku user service is unavailable",
                    errorCause = "The Shizuku user service was not connected.",
                    errorCompanion = "Rootless Store could not reach the Shell service to uninstall this plugin."
                )
            )

        // 远端卸载：请求 Shell UserService 删除 ADB 插件文件，并捕获 Binder/服务调用异常。
        val shellPluginUninstallResult = runCatching {
            shizukuUserService.uninstallShellPlugin(pluginManifest.pluginPackageName)
        }

        val isShellPluginUninstallSuccessful = shellPluginUninstallResult.getOrElse { throwable ->
            return Err(
                PluginError(
                    errorMessage = "Can't uninstall Shell plugin",
                    errorCause = throwable.stackTrace.formatAsMultilineString(),
                    errorCompanion = "The Shell service failed while removing this plugin."
                )
            )
        }

        // 远端结果判定：调用成功返回不等于卸载成功；false 时保留本地记录供后续排查。
        if (!isShellPluginUninstallSuccessful) {
            return Err(
                PluginError(
                    errorMessage = "Can't uninstall Shell plugin",
                    errorCause = "The Shell service returned false while uninstalling ${pluginManifest.pluginPackageName}.",
                    errorCompanion = "The plugin files in Shell storage could not be removed."
                )
            )
        }

        // 本地状态清理：确认 Shell 侧文件已卸载后，再删除 Plugin 与状态记录。
        return deletePluginRecords(pluginManifest.pluginId)
    }

    private suspend fun deletePluginRecords(
        pluginId: String
    ): Result<Unit, PluginError> =
        runCatching {
            pluginRepositoryImpl.deletePluginById(pluginId)
            pluginStatusRepositoryImpl.deletePluginStatus(pluginId)
        }.fold(
            onSuccess = {
                Ok(Unit)
            },
            onFailure = { throwable ->
                Err(
                    PluginError(
                        errorMessage = "Can't delete plugin records",
                        errorCause = throwable.stackTrace.formatAsMultilineString(),
                        errorCompanion = "The plugin files were removed, but Rootless Store could not delete the plugin records."
                    )
                )
            }
        )

}
