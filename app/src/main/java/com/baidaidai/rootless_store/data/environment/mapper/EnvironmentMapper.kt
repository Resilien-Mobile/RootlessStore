package com.baidaidai.rootless_store.data.environment.mapper

import com.baidaidai.rootless_store.data.environment.database.EnvironmentInfoEntity
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestLocal
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestRemote
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestRoom
import com.baidaidai.rootless_store.domain.plugin.model.PluginSource
import com.baidaidai.rootless_store.domain.plugin.model.PluginState

object EnvironmentMapper {

    fun EnvironmentInfoEntity.toEnvironmentManifestRoom (): EnvironmentManifestRoom{
        return EnvironmentManifestRoom(
            isEnabled = isEnabled,
            state = state,
            source = source,
            installedVersion = installedVersion,
            environmentRenderingName = environmentRenderingName,
            environmentPackageName = environmentPackageName,
            environmentId = environmentId,
            iconUri = iconUri,
            author = author,
            environmentDescription = environmentDescription,
            requiredEnvironment = requiredEnvironment,
            entryPoint = entryPoint,
            ldLibraryPath = ldLibraryPath,
            env = env
        )
    }

    fun EnvironmentManifestLocal.toEnvironmentInfoEntity(): EnvironmentInfoEntity{
        return EnvironmentInfoEntity(
            environmentId = environmentId,
            installedVersion = installedVersion,
            environmentRenderingName = environmentRenderingName,
            environmentPackageName = environmentPackageName,
            iconUri = iconUri,
            author = author,
            environmentDescription = environmentDescription,
            isEnabled = false,
            requiredEnvironment = requiredEnvironment,
            state = PluginState.Great,
            source = PluginSource.Local,
            entryPoint = entryPoint,
            ldLibraryPath = ldLibraryPath,
            env = env
        )
    }

    fun EnvironmentManifestRemote.toEnvironmentInfoEntity(): EnvironmentInfoEntity {
        return EnvironmentInfoEntity(
            environmentId = environmentId,
            installedVersion = installedVersion,
            environmentRenderingName = environmentRenderingName,
            environmentPackageName = environmentPackageName,
            iconUri = iconUri,
            author = author,
            environmentDescription = environmentDescription,
            isEnabled = false,
            requiredEnvironment = requiredEnvironment,
            state = PluginState.Great,
            source = PluginSource.Official,
            entryPoint = entryPoint,
            ldLibraryPath = ldLibraryPath,
            env = env
        )
    }

}
