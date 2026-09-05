package com.baidaidai.rootless_store.application.environment

import android.net.Uri
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifest
import java.io.File
import javax.inject.Inject

class ResolveEnvironmentShareUriUseCase @Inject constructor(
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl
) {
    operator fun invoke(
        environmentManifest: EnvironmentManifest
    ): Uri {

        val defaultEnvironmentCacheDirectory = androidFileSystemCapabilityGatewayImpl.getCacheEnvironmentDirectoryFile()

        // Get environment package directory -> /file/Environment/ENVIRONMENT_DIRECTORY
        val environmentPackageDirectory = androidFileSystemCapabilityGatewayImpl.resolveEnvironmentPackageDirectory(environmentManifest)

        // Detect if cache/environment available -> /cache/Environment
        val hasEnvironmentCacheDirectory = androidFileSystemCapabilityGatewayImpl.hasEnvironmentCacheDirectory()

        if (!hasEnvironmentCacheDirectory){
            androidFileSystemCapabilityGatewayImpl.ensureCacheDirectory("Environment")
        }

        // Zip to cache/environment
        // Resolve zip target -> /cache/Environment/ENVIRONMENT.zip
        val zipFile = androidFileSystemCapabilityGatewayImpl.resolveChildFile(defaultEnvironmentCacheDirectory,"${environmentManifest.environmentPackageName}.zip")

        // Write Zip Byte
        androidFileSystemCapabilityGatewayImpl.rezipFromFile(
            originPluginFile = File(environmentPackageDirectory),
            targetZipFile = zipFile
        )

        // Check if Zip is really available (option)
//      TODO("Check Zip integrity")

        // Convert the zip file to a share URI
        val shareUri = androidFileSystemCapabilityGatewayImpl.resolveShareUriFromFile(zipFile)

        return shareUri

    }

}
