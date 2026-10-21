package com.baidaidai.rootless_store.application.install

import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemDefaultOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemDeleteOperatorGatewayImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CleanupPreparedLocalPackageUseCase @Inject constructor(
    private val androidFileSystemDefaultOperatorGatewayImpl: AndroidFileSystemDefaultOperatorGatewayImpl,
    private val androidFileSystemDeleteOperatorGatewayImpl: AndroidFileSystemDeleteOperatorGatewayImpl
) {
    suspend operator fun invoke() {
        withContext(Dispatchers.IO) {
            val packageDirectory = androidFileSystemDefaultOperatorGatewayImpl
                .getExternalAppPackageCacheDirectoryFile()
            androidFileSystemDeleteOperatorGatewayImpl.deleteFileOrDirectory(packageDirectory.path)
        }
    }
}
