package com.baidaidai.rootless_store.domain.install.model

data class PackageSignatureInfo(
    val packageType: LocalPackageType,
    val displayName: String,
    val author: String,
    val validSigner: Boolean = false,
    val isSigned: Boolean? = null,
)