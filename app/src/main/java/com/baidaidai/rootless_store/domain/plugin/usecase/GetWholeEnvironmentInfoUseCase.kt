package com.baidaidai.rootless_store.domain.plugin.usecase

import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginCoreRepositoryImpl
import com.baidaidai.rootless_store.domain.plugin.manifest.EnvironmentManifestRoom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject
import kotlin.collections.map

class GetWholeEnvironmentInfoUseCase @Inject constructor(
    private val repositoryImpl: PluginCoreRepositoryImpl,
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl
) {
    operator fun invoke(): Flow<List<EnvironmentManifestRoom>> {

        val uriRegex = Regex("^https?://",RegexOption.IGNORE_CASE)

        val environmentManifestRoomListFlow = repositoryImpl.getWholeEnvironmentInfo()

        return environmentManifestRoomListFlow.map { environmentManifestRoomList ->
            environmentManifestRoomList.map { environmentManifestRoom ->

                // If user provide icon uri
                if (environmentManifestRoom.iconURI != null && !uriRegex.containsMatchIn(environmentManifestRoom.iconURI)){
                    val environmentPackageDirectory = File(androidFileSystemCapabilityGatewayImpl.getEnvironmentPackageDirectory(environmentManifestRoom))
                    val environmentIconURI = File(environmentPackageDirectory,environmentManifestRoom.iconURI).toURI()

                    environmentManifestRoom.copy(
                        iconURI = environmentIconURI.toString()
                    )
                }else{
                    environmentManifestRoom
                }

            }
        }
    }
}
