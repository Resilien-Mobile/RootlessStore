package com.baidaidai.rootless_store.data.signature.gateway

import android.content.Context
import android.util.Log
import com.baidaidai.rootless_store.data.R
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemReadOperatorGatewayImpl
import com.baidaidai.rootless_store.data.signature.repository.CaCertificateRepositoryImpl
import com.baidaidai.rootless_store.domain.install.model.LocalPackageType
import com.baidaidai.rootless_store.domain.install.model.PackageSignatureInfo
import com.baidaidai.rootless_store.domain.install.model.PackageSignatureStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import org.bouncycastle.asn1.cms.ContentInfo
import org.bouncycastle.cert.X509CertificateHolder
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cms.CMSProcessableByteArray
import org.bouncycastle.cms.CMSSignedData
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMParser
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStreamReader
import java.security.cert.CertPathBuilder
import java.security.cert.PKIXBuilderParameters
import java.security.cert.TrustAnchor
import java.security.cert.X509CertSelector
import java.security.cert.X509Certificate
import javax.inject.Inject

class PackageSignatureGatewayImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val androidFileSystemReadOperatorGatewayImpl: AndroidFileSystemReadOperatorGatewayImpl,
    private val caCertificateRepositoryImpl: CaCertificateRepositoryImpl
) {
    private val bouncyCastleProvider = BouncyCastleProvider()
    private val logTag = "PackageSignatureGateway"

    // 这个只是转换器，给 Kotlin 使用的 Java API 证书
    private val certificateConverter =
        JcaX509CertificateConverter().setProvider(bouncyCastleProvider)  // 面向 Java API 的证书转换器

    /**
     * 判断外层 Package 是否带有签名，并验证其内部 package.zip。
     *
     * 先从外层文件读取 sign.p7s 和 package.zip；没有 sign.p7s 时判定为未签名，
     * 存在签名时调用内容签名校验，并根据验签结果返回对应状态。
     *
     * @param packageContainerFile 包含 package.zip 和可选 sign.p7s 的外层 Package 文件
     * @return 未签名、签名可信或签名不可信状态
     * @since 2026.10.26
     * @version 0.1.0
     */
    suspend fun resolvePackageSignature(packageContainerFile: File): PackageSignatureStatus {

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

    /**
     * 第二阶段：校验 package.zip 的 CMS 内容签名，并将开发者证书链验证到受信任 CA
     *
     * 这个方法目前只支持使用 PEM 格式的证书进行校验，无法保证 DER 格式的证书一定按预想的方式进行
     *
     * @param packageBytes 插件包 package.zip 的 ByteArray 流
     * @param signatureBytes CMS 签名文件 sign.p7s 的 ByteArray 流
     * @param trustedCaCertificates 可信赖的根证书列表，包括但不限于内部证书、自定义根证书
     *
     * @return Boolean - 用于返回结果，是否插件包 package.zip 被合法地签名了
     *
     * @since 2026.10.26
     * @version 0.1.0
     *
     *
     *
     */
    internal suspend fun hasTrustedContentSignature(
        packageBytes: ByteArray,
        signatureBytes: ByteArray
    ): Boolean {

        val trustedCaCertificates = loadTrustedCaCertificates()

        Log.d(
            logTag,
            "Starting CMS validation. packageBytes=${packageBytes.size}, " +
                "signatureBytes=${signatureBytes.size}, trustAnchors=${trustedCaCertificates.size}"
        )

        val validationResult = runCatching {

            debugPCKS7FileFormat(signatureBytes)

            /**
             * 之前将 PEM 格式的 sign.p7s 原文直接交给 CMSSignedData 按 ASN.1 解析，
             * 导致开头的 '-' 被误读为 ASN.1 标签，并在验签前抛出 "unknown tag 13"
             *
             * 这里先将 PEM 解析为 CMS ContentInfo，再与 package.zip 的分离内容组合，
             * 以便继续进行签名校验。
             */
            val contentInfo = PEMParser(
                InputStreamReader(
                    ByteArrayInputStream(signatureBytes)
                )
            ).use { pemParser ->
                pemParser.readObject() as? ContentInfo
            } ?: throw IllegalArgumentException("sign.p7s PEM does not contain a CMS ContentInfo")

            val cmsData = CMSSignedData(
                CMSProcessableByteArray(packageBytes),
                contentInfo
            )
            val certificates = cmsData.certificates
            val signers = cmsData.signerInfos.signers

            // 获取 CMS 中所有签名者
            check(signers.isNotEmpty()) {
                "CMS contains no signers"
            }
            check(trustedCaCertificates.isNotEmpty()) {
                "No trusted CA certificates are available"
            }

            // 将 CMS(p7s文件) 携带的证书转换为 Java X509Certificate

            /**
             * BC API，拿到证书链，方便后续转化和校验
             */
            val cmsCertificateList = certificates
                .getMatches(null)
                .toList()

            val cmsCertificateListJava = cmsCertificateList.mapNotNull { certificateContent ->  // Java API 证书链
                runCatching {
                    certificateConverter.getCertificate(certificateContent)
                }.getOrElse { throwable ->
                    Log.w(logTag, "Could not convert a CMS certificate", throwable)

                    null
                }
            }
            Log.d(
                logTag,
                "CMS parsed. signers=${signers.size}, " +
                    "certificateHolders=${cmsCertificateList.size}, " +
                    "convertedCertificates=${cmsCertificateListJava.size}"
            )

            // 每个 CMS 签名只使用其 signer identifier 对应的证书进行验证。
            signers
                .withIndex()
                .any { (signerIndex, signerInformation) ->

                    /**
                     * 如果 p7s 文件里，任意一个签名者，都不能和 Rootless Store 保存的证书信息匹配上
                     * 那就直接跳出去吧
                     *
                     * 这一步只浅筛，是否有有效签名者，不进行内容计算
                     *
                     * Holder只是指针引用，保存了 Cert 的原始记录
                     */
                    val signerCertificateHolder = cmsCertificateList
                        .firstOrNull { certificateHolder ->
                            signerInformation.sid.match(certificateHolder)
                        }
                        ?: return@any false

                    runCatching {

                        val signerCertificate = certificateConverter.getCertificate(
                            signerCertificateHolder
                        )

                        // 签名者必须是有效期内的非 CA 证书，并允许数字签名用途。
                        signerCertificate.checkValidity()

                        // 验证 CMS 内容签名
                        val contentSignatureValid = signerInformation.verify(
                            JcaSimpleSignerInfoVerifierBuilder()
                                .setProvider(bouncyCastleProvider)
                                .build(signerCertificate)
                        )
                        check(contentSignatureValid){
                            "CMS content signature is invalid for signer index $signerIndex"
                        }

                        val trustedCertificatePathFound = hasTrustedCertificatePath(
                            signerCertificate = signerCertificate,
                            trustedCaCertificates = trustedCaCertificates
                        )
                        check(trustedCertificatePathFound){
                            "No trusted certificate path for signer index $signerIndex"
                        }

                        true

                    }.onFailure { throwable ->

                        Log.w(logTag, "Signer validation failed at index $signerIndex", throwable)

                    }.getOrDefault(false)

            }
        }.onFailure { throwable ->

            Log.e(logTag, "CMS validation failed before signer verification", throwable)

        }

        val isTrusted = validationResult.getOrDefault(false)

        Log.i(logTag, "CMS validation result: ${if (isTrusted) "trusted" else "untrusted"}")

        return isTrusted

    }

    /**
     * 加载本次签名校验可用的 CA 信任锚。
     *
     * 先读取应用内置 CA，再从证书 Repository 获取用户启用的证书；将 DER 证书转换为
     * X509Certificate 并检查有效期。无法解析的用户证书会记录日志并跳过。
     *
     * @return 可用于 PKIX 证书路径构建的 CA 信任锚集合
     * @since 2026.10.26
     * @version 0.1.0
     */
    internal suspend fun loadTrustedCaCertificates(): Set<X509Certificate> {
        Log.d(logTag, "Loading built-in and enabled custom CA certificates")

        val trustedCaCertificates = mutableSetOf<X509Certificate>()
        val enabledCaCertificates = caCertificateRepositoryImpl.getEnabledCaCertificates()

        runCatching {

            context
                .resources
                .openRawResource(R.raw.resilienmobile)
                .use { inputStream ->
                    PEMParser(
                        InputStreamReader(inputStream)
                    ).use { pemParser ->

                        val certificateHolder = pemParser.readObject() as X509CertificateHolder
                        trustedCaCertificates += certificateConverter.getCertificate(certificateHolder)

                    }
                }

            Log.d(logTag, "Loaded built-in CA certificate")
        }.onFailure { throwable ->
            Log.e(logTag, "Could not load built-in CA certificate", throwable)
        }

        enabledCaCertificates
            .forEachIndexed { index, caCertificate ->
                runCatching {

                    val certificate = certificateConverter.getCertificate(
                        X509CertificateHolder(caCertificate.derBytes)
                    )

                    certificate.checkValidity()

                    trustedCaCertificates += certificate

                }.onSuccess {
                    Log.d(logTag, "Loaded enabled CA certificate at index $index")
                }.onFailure { throwable ->
                    Log.w(logTag, "Skipped invalid enabled CA certificate at index $index", throwable)
                }
            }

        Log.i(logTag, "Loaded ${trustedCaCertificates.size} usable CA trust anchors")
        return trustedCaCertificates

    }

    // 将 CMS 携带的中间证书链构建到任一已启用的 CA 信任锚。
    internal fun hasTrustedCertificatePath(
        signerCertificate: X509Certificate,
        trustedCaCertificates: Set<X509Certificate>
    ): Boolean {

        return runCatching {

            val trustAnchors = trustedCaCertificates.map {
                TrustAnchor(it, null)
            }.toSet()

            val selector = X509CertSelector().apply {
                certificate = signerCertificate
            }

            val parameters = PKIXBuilderParameters(
                trustAnchors,
                selector
            ).apply {
                isRevocationEnabled = false
            }

            CertPathBuilder.getInstance(
                "PKIX",
                bouncyCastleProvider
            ).build(parameters)

        }.isSuccess

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
                packageType = LocalPackageType.Plugin,
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
            packageType = LocalPackageType.Environment,
            displayName = environmentManifest.environmentRenderingName,
            author = environmentManifest.author,
            validSigner = signatureStatus == PackageSignatureStatus.Trusted,
            isSigned = true
        )
    }

    private fun debugPCKS7FileFormat(
        signatureBytes: ByteArray
    ){
        // 检查 sign.p7s 的真实编码格式
        val prefix = signatureBytes.take(32)

        val prefixHex = prefix.joinToString(" ") { byte ->
            "%02X".format(byte.toInt() and 0xFF)
        }

        val prefixAscii = prefix.joinToString("") { byte ->
            val code = byte.toInt() and 0xFF
            if (code in 32..126) code.toChar().toString() else "."
        }

        val formatHint = when {
            signatureBytes.size >= 2 &&
                    signatureBytes[0] == 0x30.toByte() ->
                "ASN.1 SEQUENCE candidate"

            prefixAscii.startsWith("-----BEGIN") ->
                "PEM candidate"

            signatureBytes.size >= 2 &&
                    signatureBytes[0] == 0x50.toByte() &&
                    signatureBytes[1] == 0x4B.toByte() ->
                "ZIP candidate"

            else ->
                "Unknown (possibly Base64 or another format)"
        }

        Log.d(
            logTag,
            "sign.p7s header hex=$prefixHex ascii=$prefixAscii format=$formatHint"
        )
    }

    companion object {
        // 判断证书内容是否为 PEM 编码。
        fun isPemCertificate(certificateBytes: ByteArray): Boolean {
            val certificateText = certificateBytes.toString(Charsets.US_ASCII)
            return certificateText
                .trimStart('\uFEFF', ' ', '\t', '\r', '\n')
                .startsWith("-----BEGIN CERTIFICATE-----")
        }

        // 使用 Bouncy Castle 将 PEM X.509 证书解析为 DER 编码。
        fun resolvePemCertificateToDer(certificateBytes: ByteArray): ByteArray {
            val pemReader = InputStreamReader(
                ByteArrayInputStream(certificateBytes),
                Charsets.US_ASCII
            )

            return PEMParser(pemReader).use { pemParser ->
                val certificateHolder = pemParser.readObject() as? X509CertificateHolder
                    ?: error("PEM content does not contain an X.509 certificate")

                certificateHolder.encoded
            }
        }
    }
}
