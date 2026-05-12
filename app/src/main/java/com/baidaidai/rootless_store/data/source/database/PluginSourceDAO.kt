package com.baidaidai.rootless_store.data.source.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PluginSourceDAO {
    /**
     * CURD
     */

    // Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOnePluginSource(pluginSourceEntity: PluginSourceEntity)

    // Update
    @Query(
        "UPDATE pluginSource SET sourceName = :sourceName, sourceRemoteEndpoint = :sourceRemoteEndpoint " +
            "WHERE sourceID = :sourceID"
    )
    suspend fun updateOnePluginSource(sourceID: String, sourceName: String, sourceRemoteEndpoint: String)

    // Read
    @Query("SELECT * FROM pluginSource WHERE sourceID = :sourceID LIMIT 1")
    suspend fun getOnePluginSourceBySourceID(sourceID: String): PluginSourceEntity?

    @Query(value = "SELECT * FROM pluginSource")
    fun getAllPluginSources(): Flow<List<PluginSourceEntity>?>

    @Query("SELECT COUNT(*) FROM pluginSource")
    fun getPluginSourcesCount(): Flow<Int>

    // Delete
    @Delete
    suspend fun deleteOnePluginSource(pluginSourceEntity: PluginSourceEntity)

    /**
     * Other methods will be added in the future.
     */
}
