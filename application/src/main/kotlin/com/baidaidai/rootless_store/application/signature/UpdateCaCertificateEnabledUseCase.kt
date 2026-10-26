package com.baidaidai.rootless_store.application.signature

import com.baidaidai.rootless_store.data.signature.repository.CaCertificateRepositoryImpl
import javax.inject.Inject

class UpdateCaCertificateEnabledUseCase @Inject constructor(
    private val caCertificateRepositoryImpl: CaCertificateRepositoryImpl
) {
    suspend operator fun invoke(
        unixTimestamp: Long,
        enabled: Boolean
    ): Unit {
        caCertificateRepositoryImpl.updateCaCertificateEnabled(
            unixTimestamp = unixTimestamp,
            enabled = enabled
        )
    }
}