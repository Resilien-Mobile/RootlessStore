package com.baidaidai.rootless_store.application.environment

import android.net.Uri
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestRoom
import java.io.File
import javax.inject.Inject

class ResolveEnvironmentShareUriUseCase @Inject constructor(
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl
) {
    operator fun invoke(
        environmentManifestRoom: EnvironmentManifestRoom
    ): Uri {

        val defaultEnvironmentCacheDirectory = androidFileSystemCapabilityGatewayImpl.getCacheEnvironmentDirectoryFile()

        // Get environment package directory -> /file/Environment/ENVIRONMENT_DIRECTORY
        val environmentPackageDirectory = androidFileSystemCapabilityGatewayImpl.resolveEnvironmentPackageDirectory(environmentManifestRoom)

        // Detect if cache/environment available -> /cache/Environment
        val environmentCacheDirectoryAvailable = androidFileSystemCapabilityGatewayImpl.confirmEnvironmentCacheExists()

        if (!environmentCacheDirectoryAvailable){
            androidFileSystemCapabilityGatewayImpl.createCacheDir("Environment")
        }

        // Zip to cache/environment
        // Create void zip content -> /cache/Environment/ENVIRONMENT.zip
        val zipFile = androidFileSystemCapabilityGatewayImpl.createVoidFileDirectory(defaultEnvironmentCacheDirectory,"${environmentManifestRoom.environmentPackageName}.zip")

        // Write Zip Byte
        androidFileSystemCapabilityGatewayImpl.rezipFromFile(
            originPluginFile = File(environmentPackageDirectory),
            targetZipFile = zipFile
        )

        // Check if Zip is really available (option)
//      TODO("Check Zip integrity")

        // Convert from zip to ShareLink
        val shareLink = androidFileSystemCapabilityGatewayImpl.resolveShareUriFromFile(zipFile)

        return shareLink

    }

}
