package com.baidaidai.rootless_store.application.environment

import com.baidaidai.rootless_store.data.environment.repository.EnvironmentRepositoryImpl
import com.baidaidai.rootless_store.data.environment.repository.EnvironmentStatusRepositoryImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifest
import javax.inject.Inject

class UninstallEnvironmentUseCase @Inject constructor(
    private val environmentRepositoryImpl: EnvironmentRepositoryImpl,
    private val environmentStatusRepositoryImpl: EnvironmentStatusRepositoryImpl,
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl,
) {
    suspend operator fun invoke(
        environmentManifest: EnvironmentManifest
    ){
        androidFileSystemCapabilityGatewayImpl.deleteEnvironmentDirectoryByPackageName(environmentManifest.environmentPackageName)
        environmentRepositoryImpl.deleteEnvironmentById(environmentManifest.environmentId)
        environmentStatusRepositoryImpl.deleteEnvironmentStatus(environmentManifest.environmentId)
    }
}
