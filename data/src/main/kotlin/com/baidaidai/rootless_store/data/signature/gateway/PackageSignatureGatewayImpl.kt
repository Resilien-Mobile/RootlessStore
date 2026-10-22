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
import java.security.cert.X509Certificate
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

    // 第二阶段：校验 package.zip 的 CMS 内容签名，并使用内置 CA 公钥验证开发者证书。
    // 不构建 PKIX 证书链，只接受由内置证书直接签发的开发者证书。
    private fun hasTrustedContentSignature(
        packageBytes: ByteArray,
        signatureBytes: ByteArray
    ): Boolean {
        return runCatching {

            // 将 package.zip 作为待验证内容，结合 sign.p7s 解析 CMS SignedData。
            val signedData = CMSSignedData(
                CMSProcessableByteArray(packageBytes),
                signatureBytes
            )

            // 获取 CMS 中所有签名者，不限制数量。
            // 只要其中任意一位的签名受到内置 CA 信任，便可通过验证。
            val signers = signedData.signerInfos.signers
            if (signers.isEmpty()) return@runCatching false

            // 加载 Rootless Store 内置的可信 CA 证书。
            // 内置 CA 即使由上级 CA 签发，也不需要继续向上验证。
            val trustedCertificate = loadTrustedCertificate()

            // 将 CMS 携带的证书转换为 Java X509Certificate。
            val certificateConverter = JcaX509CertificateConverter()
                .setProvider(bouncyCastleProvider)

            // 遍历 CMS 携带的候选证书，不使用 sid 限定证书匹配。
            // 只要某张由内置 CA 直接签发的开发者证书可以验证任意一个 CMS 签名，即接受。
            // 核心验证签名算法
            signedData
                .certificates
                .getMatches(null)
                .any { certificateHolder ->
                    runCatching {
                        val candidate = certificateConverter.getCertificate(
                            certificateHolder
                        )

                        // 检查候选证书的颁发者是否为内置 CA。
                        // 名称匹配不代表签名合法，还需要使用 CA 公钥验证。
                        require(
                            candidate.issuerX500Principal ==
                                    trustedCertificate.subjectX500Principal
                        )

                        // 开发者证书应在有效期内，并且不能是 CA 证书。
                        candidate.checkValidity()
                        require(candidate.basicConstraints == -1)

                        // 如果有 KeyUsage 扩展，则必须允许 digitalSignature。
                        val keyUsage = candidate.keyUsage
                        require(
                            keyUsage == null || (keyUsage.isNotEmpty() && keyUsage[0])
                        )

                        // 第一步：使用内置 CA 的公钥验证开发者证书的数字签名。
                        // 不进行 PKIX 证书链构建。
                        candidate.verify(
                            trustedCertificate.publicKey,
                            bouncyCastleProvider
                        )

                        // 第二步：使用开发者证书的公钥，验证 CMS 数字签名。
                        // 同时检查 sign.p7s 与 package.zip 的内容是否匹配。
                        // 任意一个签名验证成功即可，不要求所有 CMS 签名者可信。
                        signers.any { signerInformation ->
                            runCatching {
                                signerInformation.verify(
                                    JcaSimpleSignerInfoVerifierBuilder()
                                        .setProvider(bouncyCastleProvider)
                                        .build(candidate)
                                )
                            }.getOrDefault(false)
                        }
                    }.getOrDefault(false)
                }
        }.getOrDefault(false)
    }

    // 加载 Rootless Store 内置的信任锚点证书。
    private fun loadTrustedCertificate(): X509Certificate {
        context.resources.openRawResource(R.raw.resilienmobile).use { inputStream ->
            PEMParser(InputStreamReader(inputStream)).use { pemParser ->

                val certificateHolder = pemParser.readObject() as X509CertificateHolder

                // 转换成 Java 可读懂的证书 API
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
