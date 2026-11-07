package com.baidaidai.rootless_store.application.environment

import com.baidaidai.rootless_store.domain.environment.repository.EnvironmentRepository
import com.baidaidai.rootless_store.domain.environment.repository.EnvironmentStatusRepository
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifest
import javax.inject.Inject

class UninstallEnvironmentUseCase @Inject constructor(
    private val environmentRepository: EnvironmentRepository,
    private val environmentStatusRepository: EnvironmentStatusRepository,
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl,
) {
    suspend operator fun invoke(
        environmentManifest: EnvironmentManifest
    ){
        androidFileSystemCapabilityGatewayImpl.deleteEnvironmentDirectoryByPackageName(environmentManifest.environmentPackageName)
        environmentRepository.deleteEnvironmentById(environmentManifest.environmentId)
        environmentStatusRepository.deleteEnvironmentStatus(environmentManifest.environmentId)
    }
}
