package com.baidaidai.rootless_store.data.environment.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestRoom
import kotlinx.coroutines.flow.Flow

@Dao
interface EnvironmentInfoDAO {
    /**
     * CURD
     */

    // Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOneEnvironmentInfo(environmentInfoEntity: EnvironmentInfoEntity)

    // Update
    @Query("UPDATE environmentInfo SET enabled = :enabled WHERE environmentID = :environmentID")
    suspend fun updateEnabled(environmentID: String, enabled: Boolean)

    // Read
    @Query("SELECT * FROM environmentInfo WHERE environmentID = :environmentID LIMIT 1")
    suspend fun getOneEntireEnvironmentInfoByEnvironmentID(environmentID: String): EnvironmentManifestRoom?

    @Query(value = "SELECT * FROM environmentInfo")
    fun getEntireEnvironmentManifest(): Flow<List<EnvironmentManifestRoom>>

    @Query("SELECT COUNT(*) FROM environmentInfo")
    fun getEnvironmentInfoCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM environmentInfo")
    suspend fun getTotalEnvironmentCount(): Int

    @Query("SELECT COUNT(*) FROM environmentInfo WHERE enabled = 1")
    suspend fun getEnabledEnvironmentCount(): Int

    @Query("SELECT * FROM environmentInfo WHERE enabled = 1")
    fun getEnabledEnvironment(): Flow<List<EnvironmentManifestRoom>>

    // Delete
    @Query("DELETE FROM environmentInfo WHERE environmentID = :environmentID")
    suspend fun deleteOneEnvironmentInfoByID(environmentID: String)

    /**
     * Other methods, such as update、disable、configuration change,
     * Will add in no-longer future
     */
}
