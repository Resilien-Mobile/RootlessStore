package com.baidaidai.rootless_store.application.signature

import com.baidaidai.rootless_store.data.signature.repository.CaCertificateRepositoryImpl
import com.baidaidai.rootless_store.domain.signature.model.CaCertificate
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCaCertificatesUseCase @Inject constructor(
    private val caCertificateRepositoryImpl: CaCertificateRepositoryImpl
) {
    operator fun invoke(): Flow<List<CaCertificate>> {
        return caCertificateRepositoryImpl.observeCaCertificates()
    }
}