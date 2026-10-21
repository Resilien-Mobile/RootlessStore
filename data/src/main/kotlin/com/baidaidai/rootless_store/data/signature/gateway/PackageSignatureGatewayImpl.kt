package com.baidaidai.rootless_store.data.signature.gateway

import android.content.Context
import com.baidaidai.rootless_store.data.R
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemReadOperatorGatewayImpl
import com.baidaidai.rootless_store.domain.install.model.PackageSignatureInfo
import com.baidaidai.rootless_store.domain.install.model.PackageSignatureStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import org.bouncycastle.cert.X509CertificateHolder
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cms.CMSProcessableByteArray
import org.bouncycastle.cms.CMSSignedData
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMParser
import java.io.File
import java.io.InputStreamReader
import java.security.cert.CertPathBuilder
import java.security.cert.CertStore
import java.security.cert.CollectionCertStoreParameters
import java.security.cert.PKIXBuilderParameters
import java.security.cert.TrustAnchor
import java.security.cert.X509Certificate
import java.security.cert.X509CertSelector
import javax.inject.Inject

class PackageSignatureGatewayImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val androidFileSystemReadOperatorGatewayImpl: AndroidFileSystemReadOperatorGatewayImpl
) {
    private val bouncyCastleProvider = BouncyCastleProvider()

    // 判断本地外层包是否带签名，并校验其 package.zip 内容与证书链。
    fun resolvePackageSignature(packageContainerFile: File): PackageSignatureStatus {
        val signatureBytes = androidFileSystemReadOperatorGatewayImpl
            .loadBytesInZip(packageContainerFile, "sign.p7s")
            ?: return PackageSignatureStatus.Unsigned
        val packageBytes = androidFileSystemReadOperatorGatewayImpl
            .loadBytesInZip(packageContainerFile, "package.zip")

        val isTrustedSignature = packageBytes != null &&
            hasTrustedContentSignature(
                packageBytes = packageBytes,
                signatureBytes = signatureBytes
            )

        return if (isTrustedSignature) {
            PackageSignatureStatus.Trusted
        } else {
            PackageSignatureStatus.Untrusted
        }
    }

    // 第二阶段：校验 package.zip 的 CMS 内容签名以及证书链。
    private fun hasTrustedContentSignature(
        packageBytes: ByteArray,
        signatureBytes: ByteArray
    ): Boolean {
        return runCatching {
            val signedData = CMSSignedData(
                CMSProcessableByteArray(packageBytes),
                signatureBytes
            )
            val signerInformation = signedData.signerInfos.signers.firstOrNull()
                ?: return@runCatching false

            val signerCertificateHolder = signedData.certificates
                .getMatches(null)
                .filterIsInstance<X509CertificateHolder>()
                .firstOrNull { certificateHolder ->
                    signerInformation.sid.match(certificateHolder)
                }
                ?: return@runCatching false

            val certificateConverter = JcaX509CertificateConverter()
                .setProvider(bouncyCastleProvider)
            val signerCertificate = certificateConverter.getCertificate(signerCertificateHolder)
            val trustedCertificate = loadTrustedCertificate()

            val contentSignatureMatches = signerInformation.verify(
                JcaSimpleSignerInfoVerifierBuilder()
                    .setProvider(bouncyCastleProvider)
                    .build(signerCertificate)
            )
            if (!contentSignatureMatches) {
                return@runCatching false
            }

            // 将中间 CA 作为信任锚点，允许证书链在此处结束（partial chain）。
            val certificateChain = signedData.certificates
                .getMatches(null)
                .filterIsInstance<X509CertificateHolder>()
                .map(certificateConverter::getCertificate)
            val certificateSelector = X509CertSelector().apply {
                certificate = signerCertificate
            }
            val builderParameters = PKIXBuilderParameters(
                setOf(TrustAnchor(trustedCertificate, null)),
                certificateSelector
            ).apply {
                isRevocationEnabled = false
                addCertStore(
                    CertStore.getInstance(
                        "Collection",
                        CollectionCertStoreParameters(certificateChain),
                        bouncyCastleProvider
                    )
                )
            }

            CertPathBuilder.getInstance("PKIX", bouncyCastleProvider)
                .build(builderParameters)
            true
        }.getOrDefault(false)
    }

    // 加载 Rootless Store 内置的信任锚点证书。
    private fun loadTrustedCertificate(): X509Certificate {
        context.resources.openRawResource(R.raw.resilienmobile).use { inputStream ->
            PEMParser(InputStreamReader(inputStream)).use { pemParser ->
                val certificateHolder = pemParser.readObject() as X509CertificateHolder
                return JcaX509CertificateConverter()
                    .setProvider(bouncyCastleProvider)
                    .getCertificate(certificateHolder)
            }
        }
    }

    /**
     * 理论上，这个方法不应该存在于此处，因为属于辅助方法
     */
    fun resolveSignedPackageInfo(
        packageFile: File,
        signatureStatus: PackageSignatureStatus
    ): PackageSignatureInfo {
        val pluginManifest = runCatching {
            androidFileSystemReadOperatorGatewayImpl
                .loadRawPluginManifest(packageFile)
                .also { require(it.isNotBlank()) }
                .let(androidFileSystemReadOperatorGatewayImpl::parsePluginManifest)
        }.getOrNull()

        if (pluginManifest != null) {
            return PackageSignatureInfo(
                displayName = pluginManifest.pluginRenderingName,
                author = pluginManifest.author,
                validSigner = signatureStatus == PackageSignatureStatus.Trusted,
                isSigned = true
            )
        }

        val environmentManifest = runCatching {
            androidFileSystemReadOperatorGatewayImpl
                .loadRawEnvironmentManifest(packageFile)
                .also { require(it.isNotBlank()) }
                .let(androidFileSystemReadOperatorGatewayImpl::parseEnvironmentManifest)
        }.getOrNull()
            ?: error("Signed package did not contain a supported Plugin or Environment manifest")

        return PackageSignatureInfo(
            displayName = environmentManifest.environmentRenderingName,
            author = environmentManifest.author,
            validSigner = signatureStatus == PackageSignatureStatus.Trusted,
            isSigned = true
        )
    }
}
