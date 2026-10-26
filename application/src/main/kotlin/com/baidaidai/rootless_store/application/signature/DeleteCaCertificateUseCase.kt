package com.baidaidai.rootless_store.application.signature

import com.baidaidai.rootless_store.data.signature.repository.CaCertificateRepositoryImpl
import javax.inject.Inject

class DeleteCaCertificateUseCase @Inject constructor(
    private val caCertificateRepositoryImpl: CaCertificateRepositoryImpl
) {
    suspend operator fun invoke(
        unixTimestamp: Long
    ): Unit {
        caCertificateRepositoryImpl.deleteCaCertificate(unixTimestamp)
    }
}