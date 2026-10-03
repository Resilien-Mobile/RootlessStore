package com.baidaidai.rootless_store.application.plugin

import android.util.Log
import com.baidaidai.rootless_store.data.execution.repository.PluginExecutionRepositoryImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuPermissionGatewayImpl
import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AbortPluginProcessUseCase @Inject constructor(
    private val pluginExecutionRepositoryImpl: PluginExecutionRepositoryImpl,
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val shizukuPermissionGatewayImpl: ShizukuPermissionGatewayImpl,
    private val storeStatusRepositoryImpl: StoreStatusRepositoryImpl
) {
    suspend operator fun invoke(pluginId: String) {
        val shouldUseShizuku = shouldUseShizuku(pluginId)

        if (shouldUseShizuku) {
            pluginExecutionRepositoryImpl.abortPluginProcessByShizuku(pluginId)
        } else {
            pluginExecutionRepositoryImpl.abortPluginProcess(pluginId)
        }
    }

    /**
     * 判断终止当前插件进程时是否应该通过 Shizuku 执行。
     *
     * 只有同时满足以下条件时，才会选择 Shizuku：
     * 1. App 拥有 Shizuku 权限。
     * 2. App 当前选择的执行上下文是 ADB。
     * 3. 插件要求使用 ADB 执行上下文。
     *
     * NOTE：
     * 如果执行上下文已恢复 Default，但是无法成功 Kill ，请不要来排查这里
     *
     * 通常是 Default 表面上上下文状态恢复了，但是没有提交入库
     *
     * @since 2026-10-03
     * @lastModified 2026-10-03
     */
    suspend fun shouldUseShizuku(pluginId: String): Boolean {

        // 判断条件1：检查 App 是否拥有 Shizuku 权限。
        val hasShizukuPermission = shizukuPermissionGatewayImpl.hasShizukuPermission()

        // 判断条件2：读取 App 当前选择的执行上下文，必须为 ADB。
        val selectedExecutionContext = storeStatusRepositoryImpl
            .observeExecutionContextPreference()
            .first()
        Log.d(
            "AbortPluginProcess",
            "selectedExecutionContext=$selectedExecutionContext"
        )

        // 判断条件3：读取插件所要求的执行上下文，必须为 ADB。
        val pluginRequiredEnvironment = pluginRepositoryImpl
            .findPlugin(pluginId)
            ?.requiredEnvironment

        return hasShizukuPermission &&
            selectedExecutionContext == ExecutionContext.ADB &&
            pluginRequiredEnvironment == ExecutionContext.ADB

    }
}
