package com.baidaidai.rootless_store.application.plugin

import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemDefaultOperatorGatewayImpl
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import javax.inject.Inject

class ResolvePluginWebUiUriUseCase @Inject constructor(
    private val androidFileSystemDefaultOperatorGatewayImpl: AndroidFileSystemDefaultOperatorGatewayImpl
) {
    operator fun invoke(
        pluginManifest: PluginManifest
    ): String {
        val pluginPackageDirectory = androidFileSystemDefaultOperatorGatewayImpl.resolvePluginPackageDirectory(
            pluginManifest
        )
        val webUiEntryPoint = pluginManifest.webUiEntryPoint!!

        return "$pluginPackageDirectory/$webUiEntryPoint"
    }
}
