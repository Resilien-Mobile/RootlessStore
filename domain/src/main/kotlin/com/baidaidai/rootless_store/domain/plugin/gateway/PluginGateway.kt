package com.baidaidai.rootless_store.domain.plugin.gateway

import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import java.io.File

interface PluginGateway {

    fun installPluginFromLocal(
        originFile: File
    ): Result<Unit>

    suspend fun installPluginFromMarket(
        pluginUrl: String,
        pluginManifest: PluginManifest
    )

    fun uninstallPlugin(
        pluginPackageName: String
    ): Result<Unit>

    fun setPluginEntryPointExecutable(
        pluginManifest: PluginManifest
    ): Result<Unit>

    fun setPluginExecuteFilesExecutable(
        pluginManifest: PluginManifest
    ): Result<Unit>

    fun parsePluginManifest(
        originFile: File
    ): Result<PluginManifest>

}
