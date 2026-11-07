package com.baidaidai.rootless_store.domain.fileSystem.gateway

import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifest
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest

interface AndroidFileSystemChmodOperatorGateway {

    fun setPluginEntryPointExecutable(
        pluginManifest: PluginManifest
    ): Boolean

    fun setPluginExecutableFiles(
        pluginPackageName: String,
        executableFilePath: String
    )

    fun setEnvironmentEntryPointExecutable(
        environmentManifest: EnvironmentManifest
    ): Boolean

}
