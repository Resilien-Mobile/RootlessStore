package com.baidaidai.rootless_store.application.install

import android.net.Uri
import com.baidaidai.rootless_store.core.util.formatAsMultilineString
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCreateOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemDefaultOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemDeleteOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemReadOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemUnzipOperatorGatewayImpl
import com.baidaidai.rootless_store.data.signature.gateway.PackageSignatureGatewayImpl
import com.baidaidai.rootless_store.domain.install.model.PackageSignatureStatus
import com.baidaidai.rootless_store.domain.install.model.PackageSignatureInfo
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class PrepareLocalPackageInstallUseCase @Inject constructor(
    private val androidFileSystemCreateOperatorGatewayImpl: AndroidFileSystemCreateOperatorGatewayImpl,
    private val androidFileSystemDefaultOperatorGatewayImpl: AndroidFileSystemDefaultOperatorGatewayImpl,
    private val androidFileSystemDeleteOperatorGatewayImpl: AndroidFileSystemDeleteOperatorGatewayImpl,
    private val androidFileSystemReadOperatorGatewayImpl: AndroidFileSystemReadOperatorGatewayImpl,
    private val androidFileSystemUnzipOperatorGatewayImpl: AndroidFileSystemUnzipOperatorGatewayImpl,
    private val packageSignatureGatewayImpl: PackageSignatureGatewayImpl
) {

    suspend operator fun invoke(uri: Uri): Result<PackageSignatureInfo?, PluginError> {
        return withContext(Dispatchers.IO) {
            try {
                preparePackage(uri)
            } catch (cancellationException: CancellationException) {
                cleanupPackageDirectory()
                throw cancellationException
            } catch (throwable: Exception) {
                cleanupPackageDirectory()
                Err(
                    PluginError(
                        errorMessage = "Can't prepare local package",
                        errorCause = throwable.stackTrace.formatAsMultilineString(),
                        errorCompanion = "Rootless Store could not copy or normalize the selected package for installation."
                    )
                )
            }
        }
    }

    private suspend fun preparePackage(uri: Uri): Result<PackageSignatureInfo?, PluginError> {

        // 准备稳定的 cache/Package 工作区，并先将 SAF 内容落到本地文件。
        val packageDirectory = androidFileSystemDefaultOperatorGatewayImpl
            .getExternalAppPackageCacheDirectoryFile()
        val previousPackageDirectoryDeleted = androidFileSystemDeleteOperatorGatewayImpl
            .deleteFileOrDirectory(packageDirectory.path)

        // 确认清除空的文件夹，并且文件夹存在且为空
        check(previousPackageDirectoryDeleted || !packageDirectory.exists()) {
            "Could not clear package preparation directory: ${packageDirectory.path}"
        }
        check(packageDirectory.mkdirs() || packageDirectory.isDirectory) {
            "Could not create package preparation directory: ${packageDirectory.path}"
        }

        // 创建空文件指针，并拷贝原始 Package 进入待操作区
        val sourcePackageFile = File(packageDirectory, "source.zip")
        val normalizedPackageFile = File(packageDirectory, "package.zip")
        androidFileSystemCreateOperatorGatewayImpl.copyUriToFile(
            originFileUri = uri,
            targetFile = sourcePackageFile
        )

        // Magisk 模块跳过签名识别，保留给原有 Magisk 安装流程解析。
        val magiskModuleProp = androidFileSystemReadOperatorGatewayImpl
            .loadRawMagiskModuleProp(sourcePackageFile)
        if (magiskModuleProp.isNotBlank()) {
            copyFileToFile(sourcePackageFile, normalizedPackageFile)
            return Ok(null)
        }

        // 根据外层签名状态选择解包或 2.x 无签名兼容路径。
        val packageSignatureStatus = packageSignatureGatewayImpl
            .resolvePackageSignature(sourcePackageFile)
        if (packageSignatureStatus == PackageSignatureStatus.Unsigned) {

            // 2.x 兼容：无签名包复制并统一命名
            copyFileToFile(sourcePackageFile, normalizedPackageFile)
            return Ok(null)

        }

        // 签名包只解出约定的内层包和签名文件，安装流程后续只接收 package.zip。
        androidFileSystemUnzipOperatorGatewayImpl.extractFilesFromZip(
            originFile = sourcePackageFile,
            targetDirectory = packageDirectory,
            fileNames = setOf("package.zip", "sign.p7s")
        )
        check(normalizedPackageFile.isFile) { "Signed package.zip was not found" }
        check(File(packageDirectory, "sign.p7s").isFile) { "Signed sign.p7s was not found" }
        check(sourcePackageFile.delete()) { "Could not remove outer signed package" }

        // 从已归一化的内层包构造签名确认信息。
        val packageSingerInfo = packageSignatureGatewayImpl.resolveSignedPackageInfo(
            packageFile = normalizedPackageFile,
            signatureStatus = packageSignatureStatus
        )
        return Ok(packageSingerInfo)
    }

    private fun copyFileToFile(
        sourcePackageFile: File,
        normalizedPackageFile: File
    ) {
        androidFileSystemCreateOperatorGatewayImpl.copyFileToFile(
            originFile = sourcePackageFile,
            targetFile = normalizedPackageFile
        )
        check(sourcePackageFile.delete()) { "Could not remove source package copy" }
    }

    private fun cleanupPackageDirectory() {
        val packageDirectory = androidFileSystemDefaultOperatorGatewayImpl
            .getExternalAppPackageCacheDirectoryFile()
        androidFileSystemDeleteOperatorGatewayImpl.deleteFileOrDirectory(packageDirectory.path)
    }
}
