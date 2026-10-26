package com.baidaidai.rootless_store.data.signature.mapper

import com.baidaidai.rootless_store.data.signature.database.CaCertificateEntity
import com.baidaidai.rootless_store.domain.signature.model.CaCertificate

object CaCertificateMapper {
    fun CaCertificateEntity.toCaCertificate(): CaCertificate {
        return CaCertificate(
            unixTimestamp = unixTimestamp,
            name = name,
            derBytes = derBytes,
            enabled = enabled
        )
    }

    fun CaCertificate.toCaCertificateEntity(): CaCertificateEntity {
        return CaCertificateEntity(
            unixTimestamp = unixTimestamp,
            name = name,
            derBytes = derBytes,
            enabled = enabled
        )
    }
}