package com.baidaidai.rootless_store.application.install

import com.baidaidai.rootless_store.domain.fileSystem.gateway.AndroidFileSystemDefaultOperatorGateway
import com.baidaidai.rootless_store.domain.fileSystem.gateway.AndroidFileSystemDeleteOperatorGateway
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CleanupPreparedLocalPackageUseCase @Inject constructor(
    private val androidFileSystemDefaultOperatorGateway: AndroidFileSystemDefaultOperatorGateway,
    private val androidFileSystemDeleteOperatorGateway: AndroidFileSystemDeleteOperatorGateway
) {
    suspend operator fun invoke() {
        withContext(Dispatchers.IO) {
            val packageDirectory = androidFileSystemDefaultOperatorGateway
                .getExternalAppPackageCacheDirectoryFile()
            androidFileSystemDeleteOperatorGateway.deleteFileOrDirectory(packageDirectory.path)
        }
    }
}
