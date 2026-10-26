package com.baidaidai.rootless_store.domain.signature.repository

import com.baidaidai.rootless_store.domain.signature.model.CaCertificate
import kotlinx.coroutines.flow.Flow

interface CaCertificateRepository {
    // Create
    suspend fun addCaCertificate(caCertificate: CaCertificate)

    // Read
    suspend fun findCaCertificate(unixTimestamp: Long): CaCertificate?

    fun observeCaCertificates(): Flow<List<CaCertificate>>
    fun observeEnabledCaCertificates(): Flow<List<CaCertificate>>
    suspend fun getEnabledCaCertificates(): List<CaCertificate>

    // Update
    suspend fun updateCaCertificate(caCertificate: CaCertificate)
    suspend fun updateCaCertificateEnabled(
        unixTimestamp: Long,
        enabled: Boolean
    )

    // Delete
    suspend fun deleteCaCertificate(unixTimestamp: Long)
}