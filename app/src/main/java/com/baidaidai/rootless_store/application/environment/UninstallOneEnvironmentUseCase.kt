package com.baidaidai.rootless_store.application.environment

import com.baidaidai.rootless_store.data.environment.repository.EnvironmentRepositoryImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestRoom
import javax.inject.Inject

class UninstallOneEnvironmentUseCase @Inject constructor(
    private val environmentRepositoryImpl: EnvironmentRepositoryImpl,
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl,
) {
    suspend operator fun invoke(
        environmentManifestRoom: EnvironmentManifestRoom
    ){
        androidFileSystemCapabilityGatewayImpl.deleteEnvironmentDirectoryByPackageName(environmentManifestRoom.environmentPackageName)
        environmentRepositoryImpl.deleteOneEnvironmentInfoByID(environmentManifestRoom.environmentID)
    }
}