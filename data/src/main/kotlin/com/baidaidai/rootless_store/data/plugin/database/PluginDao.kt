package com.baidaidai.rootless_store.data.plugin.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PluginDao {
    /**
     * CRUD
     */

    // Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlugin(pluginEntity: PluginEntity)

    // Read
    @Query("SELECT * FROM pluginInfo WHERE pluginID = :pluginId LIMIT 1")
    suspend fun findPluginById(pluginId: String): PluginEntity?

    @Query(value = "SELECT * FROM pluginInfo")
    fun observePlugins(): Flow<List<PluginEntity>>

    @Query("SELECT COUNT(*) FROM pluginInfo")
    fun observePluginCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM pluginInfo")
    suspend fun getPluginCount(): Int

    // Delete
    @Query("DELETE FROM pluginInfo WHERE pluginID = :pluginId")
    suspend fun deletePluginById(pluginId: String)
}
