package com.baidaidai.rootless_store.application.plugin

import android.net.Uri
import com.baidaidai.rootless_store.core.util.formatAsMultilineString
import com.baidaidai.rootless_store.data.plugin.gateway.PluginGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import com.baidaidai.rootless_store.domain.plugin.model.PluginOrigin
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import javax.inject.Inject

class InstallPluginUseCase @Inject constructor(
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val pluginStatusRepositoryImpl: PluginStatusRepositoryImpl,
    private val pluginGatewayImpl: PluginGatewayImpl,
    private val installShellPluginUseCase: InstallShellPluginUseCase
) {
    suspend operator fun invoke(uri: Uri): Result<Unit, PluginError> {

        val pluginManifest: PluginManifest = pluginGatewayImpl
            .parsePluginManifest(uri)
            .getOrElse { throwable ->
                return Err(
                    PluginError(
                        errorMessage = "Can't parse plugin manifest",
                        errorCause = throwable.stackTrace.formatAsMultilineString(),
                        errorCompanion = "Rootless Store could not read PluginManifest.json, or the manifest schema does not match the current plugin format."
                    )
                )
            }

        if (pluginManifest.requiredEnvironment == ExecutionContext.ADB){
            return installShellPluginUseCase(
                uri = uri,
                pluginManifest = pluginManifest
            )
        }else{

            // Un-Zip, Install Plugin
            pluginGatewayImpl
                .installPluginFromLocal(uri)
                .getOrElse { throwable ->
                    return Err(
                        PluginError(
                            errorMessage = "Can't Un-Zip / install Plugin",
                            errorCause = throwable.stackTrace.formatAsMultilineString(),
                            errorCompanion = "The package was recognized as a plugin, but Rootless Store could not extract it into app storage."
                        )
                    )
                }

            // Set Execute-able, Made it can call and use
            pluginGatewayImpl
                .setPluginEntryPointExecutable(pluginManifest)
                .getOrElse { throwable ->
                    return Err(
                        PluginError(
                            errorMessage = "Can't set plugin executable",
                            errorCause = throwable.stackTrace.formatAsMultilineString(),
                            errorCompanion = "The plugin was extracted, but Rootless Store could not mark its entry point executable."
                        )
                    )
                }

            // Add Data, Register Plugin
            pluginRepositoryImpl.addPlugin(pluginManifest)
            pluginStatusRepositoryImpl.registerPluginStatus(pluginManifest.pluginId, PluginOrigin.Local)

            return Ok(Unit)
        }
    }

    /**
     * 安装一个提供 WebUI 的 ADB 插件内部副本。
     *
     * 该方法仅用于 `侧载`  操作，沿用普通的内部插件安装流程，
     * 但不会写入插件信息或插件状态记录。只有当 Shell 插件安装和本方法都成功后，
     * 调用方才会注册该插件。
     *
     * @since 2026-10-04
     * @lastModified 2026-10-04
     */
    internal operator fun invoke(
        uri: Uri,
        pluginManifest: PluginManifest
    ): Result<Unit, PluginError> {
        pluginGatewayImpl
            .installPluginFromLocal(uri)
            .getOrElse { throwable ->
                return Err(
                    PluginError(
                        errorMessage = "Can't Un-Zip / install Plugin",
                        errorCause = throwable.stackTrace.formatAsMultilineString(),
                        errorCompanion = "The package was recognized as a plugin, but Rootless Store could not extract it into app storage."
                    )
                )
            }

        pluginGatewayImpl
            .setPluginEntryPointExecutable(pluginManifest)
            .getOrElse { throwable ->
                return Err(
                    PluginError(
                        errorMessage = "Can't set plugin executable",
                        errorCause = throwable.stackTrace.formatAsMultilineString(),
                        errorCompanion = "The plugin was extracted, but Rootless Store could not mark its entry point executable."
                    )
                )
            }

        return Ok(Unit)
    }
}
