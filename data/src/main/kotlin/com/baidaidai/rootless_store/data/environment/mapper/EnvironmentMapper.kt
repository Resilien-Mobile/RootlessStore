package com.baidaidai.rootless_store.data.environment.mapper

import com.baidaidai.rootless_store.data.environment.database.EnvironmentEntity
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestLocal
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestRemote
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestRoom
import com.baidaidai.rootless_store.domain.plugin.model.PluginOrigin
import com.baidaidai.rootless_store.domain.plugin.model.PluginState

object EnvironmentMapper {

    fun EnvironmentEntity.toEnvironmentManifestRoom (): EnvironmentManifestRoom{
        return EnvironmentManifestRoom(
            isEnabled = isEnabled,
            state = state,
            origin = origin,
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

    fun EnvironmentManifestLocal.toEnvironmentEntity(): EnvironmentEntity{
        return EnvironmentEntity(
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
            origin = PluginOrigin.Local,
            entryPoint = entryPoint,
            ldLibraryPath = ldLibraryPath,
            env = env
        )
    }

    fun EnvironmentManifestRemote.toEnvironmentEntity(): EnvironmentEntity {
        return EnvironmentEntity(
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
            origin = PluginOrigin.Official,
            entryPoint = entryPoint,
            ldLibraryPath = ldLibraryPath,
            env = env
        )
    }

}
