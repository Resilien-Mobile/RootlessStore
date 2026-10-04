package com.baidaidai.rootless_store.application.execute

import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuPermissionGatewayImpl
import com.baidaidai.rootless_store.domain.execution.model.ExecutionResult
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExecutePluginUseCase @Inject constructor(
    private val executePluginByShizukuUseCase: ExecutePluginByShizukuUseCase,
    private val executePluginByAppShellUseCase: ExecutePluginByAppShellUseCase,
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val shizukuPermissionGatewayImpl: ShizukuPermissionGatewayImpl,
) {
    suspend operator fun invoke(
        pluginId: String
    ): Flow<ExecutionResult> {

        val shouldUseShizuku = shouldUseShizuku(pluginId)

        // Judge if needs use Shizuku
        return if (shouldUseShizuku) {
            executePluginByShizukuUseCase(pluginId)
        } else {
            executePluginByAppShellUseCase(pluginId)
        }

    }

    /**
     * 判断当前插件是否应该通过 Shizuku 执行。
     *
     * 只有同时满足以下条件时，才会选择 Shizuku：
     * 1. App 拥有 Shizuku 权限。
     * 2. 插件要求使用 ADB 执行上下文。
     *
     * @since 2026-10-03
     * @lastModified 2026-10-04
     */
    suspend fun shouldUseShizuku(pluginId: String): Boolean {

        // 判断条件1：检查 App 是否拥有 Shizuku 权限。
        val hasShizukuPermission = shizukuPermissionGatewayImpl.hasShizukuPermission()

        // 判断条件2：读取插件所要求的执行上下文，必须为 ADB。
        val pluginRequiredEnvironment = pluginRepositoryImpl
            .findPlugin(pluginId)
            ?.requiredEnvironment

        return hasShizukuPermission && pluginRequiredEnvironment == ExecutionContext.ADB
    }
}
