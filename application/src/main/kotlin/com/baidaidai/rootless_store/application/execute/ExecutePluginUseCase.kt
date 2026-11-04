package com.baidaidai.rootless_store.application.execute

import android.content.Context
import com.baidaidai.rootless_store.application.R
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuPermissionGatewayImpl
import com.baidaidai.rootless_store.domain.execution.model.ExecutionResult
import com.baidaidai.rootless_store.domain.execution.error.ExecutionError
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExecutePluginUseCase @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val executePluginByShizukuUseCase: ExecutePluginByShizukuUseCase,
    private val executePluginByAppShellUseCase: ExecutePluginByAppShellUseCase,
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val shizukuPermissionGatewayImpl: ShizukuPermissionGatewayImpl,
) {
    suspend operator fun invoke(
        pluginId: String
    ): Result<Flow<ExecutionResult>, ExecutionError> {

        val shouldUseShizuku = shouldUseShizuku(pluginId)

        if (!shouldUseShizuku) {
            return Ok(executePluginByAppShellUseCase(pluginId))
        }

        if (!shizukuPermissionGatewayImpl.canUseShizuku()) {
            return Err(
                ExecutionError(
                    errorMessage = "Oops!",
                    errorCause = "",
                    errorCompanion = context.getString(
                        R.string.execute_plugin_shizuku_permission_denied
                    )
                )
            )
        }

        return Ok(executePluginByShizukuUseCase(pluginId))
    }

    /**
     * 判断插件声明的执行环境是否要求通过 Shizuku 执行。
     *
     * 此方法只读取插件的执行环境声明，不检查 App 当前是否拥有 Shizuku 权限。
     *
     * @since 2026-10-03
     * @lastModified 2026-11-04
     */
    suspend fun shouldUseShizuku(
        pluginId: String
    ): Boolean {
        val pluginManifest = requireNotNull(
            value = pluginRepositoryImpl.findPlugin(pluginId)
        ) {
            "Plugin not found: $pluginId"
        }

        return pluginManifest.requiredEnvironment == ExecutionContext.ADB
    }

}
