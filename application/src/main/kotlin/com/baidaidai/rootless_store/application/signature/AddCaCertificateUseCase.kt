package com.baidaidai.rootless_store.application.signature

import android.net.Uri
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemReadOperatorGatewayImpl
import com.baidaidai.rootless_store.data.signature.repository.CaCertificateRepositoryImpl
import com.baidaidai.rootless_store.data.signature.gateway.PackageSignatureGatewayImpl
import com.baidaidai.rootless_store.domain.signature.model.CaCertificate
import javax.inject.Inject

class AddCaCertificateUseCase @Inject constructor(
    private val androidFileSystemReadOperatorGatewayImpl: AndroidFileSystemReadOperatorGatewayImpl,
    private val caCertificateRepositoryImpl: CaCertificateRepositoryImpl
) {
    suspend operator fun invoke(
        name: String,
        certificateUri: Uri
    ) {
        val certificateBytes = androidFileSystemReadOperatorGatewayImpl
            .loadBytesFromUri(certificateUri)
            ?: error("Unable to read the selected CA certificate")

        val certificateDerBytes = if (
            PackageSignatureGatewayImpl.isPemCertificate(certificateBytes)
        ) {
            PackageSignatureGatewayImpl.resolvePemCertificateToDer(certificateBytes)
        } else {
            certificateBytes
        }

        val caCertificate = CaCertificate(
            unixTimestamp = System.currentTimeMillis(),
            name = name,
            derBytes = certificateDerBytes,
            enabled = true
        )

        caCertificateRepositoryImpl.addCaCertificate(caCertificate)

    }
}