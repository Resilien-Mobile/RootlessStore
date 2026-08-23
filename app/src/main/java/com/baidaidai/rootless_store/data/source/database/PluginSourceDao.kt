package com.baidaidai.rootless_store.data.source.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PluginSourceDao {
    /**
     * CRUD
     */

    // Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPluginSource(pluginSourceEntity: PluginSourceEntity)

    // Update
    @Query(
        "UPDATE pluginSource SET sourceName = :sourceName, sourceRemoteEndpoint = :sourceRemoteEndpoint " +
            "WHERE sourceID = :sourceId"
    )
    suspend fun updatePluginSource(sourceId: String, sourceName: String, sourceRemoteEndpoint: String)

    // Read
    @Query("SELECT * FROM pluginSource WHERE sourceID = :sourceId LIMIT 1")
    suspend fun findPluginSourceById(sourceId: String): PluginSourceEntity?

    @Query(value = "SELECT * FROM pluginSource")
    fun observePluginSources(): Flow<List<PluginSourceEntity>?>

    @Query("SELECT COUNT(*) FROM pluginSource")
    fun observePluginSourceCount(): Flow<Int>

    // Delete
    @Delete
    suspend fun deletePluginSource(pluginSourceEntity: PluginSourceEntity)

    /**
     * Other methods will be added in the future.
     */
}
