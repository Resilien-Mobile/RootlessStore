package com.baidaidai.rootless_store.application.environment

import com.baidaidai.rootless_store.data.environment.repository.EnvironmentRepositoryImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class ObserveEnvironmentsUseCase @Inject constructor(
    private val environmentRepositoryImpl: EnvironmentRepositoryImpl,
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl
) {
    operator fun invoke(): Flow<List<EnvironmentManifest>> {

        val uriRegex = Regex("^https?://", RegexOption.IGNORE_CASE)

        return environmentRepositoryImpl.observeEnvironments().map { environmentManifestList ->
            environmentManifestList.map { environmentManifest ->

                // If user provide icon uri
                if (environmentManifest.iconUri != null && !uriRegex.containsMatchIn(
                        environmentManifest.iconUri!!
                    )
                ) {
                    val environmentPackageDirectory = File(
                        androidFileSystemCapabilityGatewayImpl.resolveEnvironmentPackageDirectory(environmentManifest)
                    )
                    val environmentIconUri = File(environmentPackageDirectory, environmentManifest.iconUri!!).toURI()

                    environmentManifest.copy(
                        iconUri = environmentIconUri.toString()
                    )
                } else {
                    environmentManifest
                }

            }
        }
    }
}
