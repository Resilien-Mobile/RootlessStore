package com.baidaidai.rootless_store.data.environment.mapper

import com.baidaidai.rootless_store.data.environment.database.EnvironmentEntity
import com.baidaidai.rootless_store.data.environment.database.EnvironmentStatusEntity
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifest
import com.baidaidai.rootless_store.domain.environment.model.EnvironmentStatus

object EnvironmentMapper {

    fun EnvironmentManifest.toEnvironmentEntity(): EnvironmentEntity {
        return EnvironmentEntity(
            environmentId = environmentId,
            installedVersion = installedVersion,
            environmentRenderingName = environmentRenderingName,
            environmentPackageName = environmentPackageName,
            iconUri = iconUri,
            author = author,
            environmentDescription = environmentDescription,
            requiredEnvironment = requiredEnvironment,
            entryPoint = entryPoint,
            ldLibraryPath = ldLibraryPath,
            env = env
        )
    }

    fun EnvironmentEntity.toEnvironmentManifest(): EnvironmentManifest {
        return EnvironmentManifest(
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
            // environmentUrl is not persisted
        )
    }

    fun EnvironmentStatusEntity.toEnvironmentStatus(): EnvironmentStatus {
        return EnvironmentStatus(
            environmentId = environmentId,
            isEnabled = isEnabled,
            state = state,
            origin = origin
        )
    }

    fun EnvironmentStatus.toEnvironmentStatusEntity(): EnvironmentStatusEntity {
        return EnvironmentStatusEntity(
            environmentId = environmentId,
            isEnabled = isEnabled,
            state = state,
            origin = origin
        )
    }
}
