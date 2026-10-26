package com.baidaidai.rootless_store.data.signature.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CaCertificateDao {
    // Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCaCertificate(caCertificateEntity: CaCertificateEntity)

    // Read
    @Query("SELECT * FROM CaCertificateEntity WHERE unixTimestamp = :unixTimestamp LIMIT 1")
    suspend fun findCaCertificate(unixTimestamp: Long): CaCertificateEntity?

    @Query("SELECT * FROM CaCertificateEntity WHERE unixTimestamp = :unixTimestamp LIMIT 1")
    fun observeCaCertificate(unixTimestamp: Long): Flow<CaCertificateEntity?>

    @Query("SELECT * FROM CaCertificateEntity")
    fun observeCaCertificates(): Flow<List<CaCertificateEntity>>

    @Query("SELECT * FROM CaCertificateEntity WHERE enabled = 1")
    fun observeEnabledCaCertificates(): Flow<List<CaCertificateEntity>>

    @Query("SELECT * FROM CaCertificateEntity WHERE enabled = 1")
    suspend fun getEnabledCaCertificateEntities(): List<CaCertificateEntity>

    // Update
    @Update
    suspend fun updateCaCertificate(caCertificateEntity: CaCertificateEntity)

    @Query("UPDATE CaCertificateEntity SET enabled = :enabled WHERE unixTimestamp = :unixTimestamp")
    suspend fun updateCaCertificateEnabled(
        unixTimestamp: Long,
        enabled: Boolean
    )

    // Delete
    @Query("DELETE FROM CaCertificateEntity WHERE unixTimestamp = :unixTimestamp")
    suspend fun deleteCaCertificate(unixTimestamp: Long)
}