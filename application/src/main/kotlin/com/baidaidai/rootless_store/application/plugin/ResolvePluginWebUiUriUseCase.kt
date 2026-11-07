package com.baidaidai.rootless_store.application.plugin

import com.baidaidai.rootless_store.domain.fileSystem.gateway.AndroidFileSystemDefaultOperatorGateway
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import javax.inject.Inject

class ResolvePluginWebUiUriUseCase @Inject constructor(
    private val androidFileSystemDefaultOperatorGateway: AndroidFileSystemDefaultOperatorGateway
) {
    operator fun invoke(
        pluginManifest: PluginManifest
    ): String {
        val pluginPackageDirectory = androidFileSystemDefaultOperatorGateway.resolvePluginPackageDirectory(
            pluginManifest
        )
        val webUiEntryPoint = pluginManifest.webUiEntryPoint!!

        return "$pluginPackageDirectory/$webUiEntryPoint"
    }
}
