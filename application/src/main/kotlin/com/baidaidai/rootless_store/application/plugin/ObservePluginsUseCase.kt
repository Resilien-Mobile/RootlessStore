package com.baidaidai.rootless_store.application.plugin

import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class ObservePluginsUseCase @Inject constructor(
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl
) {
    operator fun invoke(): Flow<List<PluginManifest>> {

        /**
         * This is merely a temporary solution for version 1.0.0
         * Starting from version 2.0.0
         * we will no longer use regex but instead rely directly on property checks
         */

        val uriRegex = Regex("^https?://", RegexOption.IGNORE_CASE)

        return pluginRepositoryImpl.observePlugins().map { pluginManifestList ->
            pluginManifestList.map { pluginManifest ->

                // If user provide icon uri
                if (pluginManifest.iconUri != null && !uriRegex.containsMatchIn(
                        pluginManifest.iconUri!!
                    )
                ) {
                    val pluginPackageDirectory = File(
                        androidFileSystemCapabilityGatewayImpl.resolvePluginPackageDirectory(pluginManifest)
                    )
                    val pluginIconUri = File(pluginPackageDirectory, pluginManifest.iconUri!!).toURI()

                    pluginManifest.copy(
                        iconUri = pluginIconUri.toString()
                    )
                } else {
                    pluginManifest
                }

            }
        }
    }
}
