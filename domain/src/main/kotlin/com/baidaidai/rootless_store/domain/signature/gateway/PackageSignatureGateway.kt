package com.baidaidai.rootless_store.domain.signature.gateway

import com.baidaidai.rootless_store.domain.install.model.PackageSignatureInfo
import com.baidaidai.rootless_store.domain.install.model.PackageSignatureStatus
import java.io.File

interface PackageSignatureGateway {

    suspend fun resolvePackageSignature(
        packageContainerFile: File
    ): PackageSignatureStatus

    fun resolveSignedPackageInfo(
        packageFile: File,
        signatureStatus: PackageSignatureStatus
    ): PackageSignatureInfo

}
