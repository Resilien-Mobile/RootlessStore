package com.baidaidai.rootless_store.data.environment.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EnvironmentStatusDao {

    // Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnvironmentStatus(environmentStatusEntity: EnvironmentStatusEntity)

    // Read
    @Query("SELECT * FROM environmentStatus WHERE environmentID = :environmentId LIMIT 1")
    suspend fun findEnvironmentStatusById(environmentId: String): EnvironmentStatusEntity?

    @Query("SELECT * FROM environmentStatus WHERE environmentID = :environmentId LIMIT 1")
    fun observeEnvironmentStatusById(environmentId: String): Flow<EnvironmentStatusEntity?>

    @Query(value = "SELECT * FROM environmentStatus")
    fun observeEnvironmentStatuses(): Flow<List<EnvironmentStatusEntity>>

    @Query(value = "SELECT * FROM environmentStatus WHERE enabled = 1")
    fun observeEnabledEnvironmentStatuses(): Flow<List<EnvironmentStatusEntity>>

    @Query("SELECT COUNT(*) FROM environmentStatus WHERE enabled = 1")
    suspend fun getEnabledEnvironmentCount(): Int

    // Update
    @Query("UPDATE environmentStatus SET enabled = :isEnabled WHERE environmentID = :environmentId")
    suspend fun updateEnvironmentEnabled(environmentId: String, isEnabled: Boolean)

    // Delete
    @Query("DELETE FROM environmentStatus WHERE environmentID = :environmentId")
    suspend fun deleteEnvironmentStatusById(environmentId: String)
}
