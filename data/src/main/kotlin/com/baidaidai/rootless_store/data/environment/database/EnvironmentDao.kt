package com.baidaidai.rootless_store.data.environment.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EnvironmentDao {
    /**
     * CRUD
     */

    // Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnvironment(environmentEntity: EnvironmentEntity)

    // Update
    @Query("UPDATE environmentInfo SET enabled = :isEnabled WHERE environmentID = :environmentId")
    suspend fun updateEnvironmentEnabled(environmentId: String, isEnabled: Boolean)

    // Read
    @Query("SELECT * FROM environmentInfo WHERE environmentID = :environmentId LIMIT 1")
    suspend fun findEnvironmentById(environmentId: String): EnvironmentEntity?

    @Query(value = "SELECT * FROM environmentInfo")
    fun observeEnvironments(): Flow<List<EnvironmentEntity>>

    @Query("SELECT COUNT(*) FROM environmentInfo")
    fun observeEnvironmentCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM environmentInfo")
    suspend fun getEnvironmentCount(): Int

    @Query("SELECT COUNT(*) FROM environmentInfo WHERE enabled = 1")
    suspend fun getEnabledEnvironmentCount(): Int

    @Query("SELECT * FROM environmentInfo WHERE enabled = 1")
    fun observeEnabledEnvironments(): Flow<List<EnvironmentEntity>>

    // Delete
    @Query("DELETE FROM environmentInfo WHERE environmentID = :environmentId")
    suspend fun deleteEnvironmentById(environmentId: String)

    /**
     * Other methods, such as update、disable、configuration change,
     * Will add in no-longer future
     */
}
