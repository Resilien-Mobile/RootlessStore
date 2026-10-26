package com.baidaidai.rootless_store.data.signature.repository

import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.data.signature.mapper.CaCertificateMapper.toCaCertificate
import com.baidaidai.rootless_store.data.signature.mapper.CaCertificateMapper.toCaCertificateEntity
import com.baidaidai.rootless_store.domain.signature.model.CaCertificate
import com.baidaidai.rootless_store.domain.signature.repository.CaCertificateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CaCertificateRepositoryImpl @Inject constructor(
    rootlessStoreDatabase: RootlessStoreDatabase
) : CaCertificateRepository {
    private val caCertificateDao = rootlessStoreDatabase.caCertificateDao()

    // Create
    override suspend fun addCaCertificate(caCertificate: CaCertificate) {
        caCertificateDao.insertCaCertificate(caCertificate.toCaCertificateEntity())
    }

    // Read
    override suspend fun findCaCertificate(unixTimestamp: Long): CaCertificate? {
        return caCertificateDao
            .findCaCertificate(unixTimestamp)
            ?.toCaCertificate()
    }

    override fun observeCaCertificates(): Flow<List<CaCertificate>> {
        return caCertificateDao
            .observeCaCertificates()
            .map { caCertificateEntities ->
                caCertificateEntities.map { caCertificateEntity ->
                    caCertificateEntity.toCaCertificate()
                }
            }
    }

    override fun observeEnabledCaCertificates(): Flow<List<CaCertificate>> {
        return caCertificateDao
            .observeEnabledCaCertificates()
            .map { caCertificateEntities ->
                caCertificateEntities.map { caCertificateEntity ->
                    caCertificateEntity.toCaCertificate()
                }
            }
    }

    override suspend fun getEnabledCaCertificates(): List<CaCertificate> {
        return caCertificateDao
            .getEnabledCaCertificateEntities()
            .map { caCertificateEntity ->
                caCertificateEntity.toCaCertificate()
            }
    }

    // Update
    override suspend fun updateCaCertificate(caCertificate: CaCertificate) {
        caCertificateDao.updateCaCertificate(caCertificate.toCaCertificateEntity())
    }

    override suspend fun updateCaCertificateEnabled(
        unixTimestamp: Long,
        enabled: Boolean
    ) {
        caCertificateDao.updateCaCertificateEnabled(
            unixTimestamp = unixTimestamp,
            enabled = enabled
        )
    }

    // Delete
    override suspend fun deleteCaCertificate(unixTimestamp: Long) {
        caCertificateDao.deleteCaCertificate(unixTimestamp)
    }
}