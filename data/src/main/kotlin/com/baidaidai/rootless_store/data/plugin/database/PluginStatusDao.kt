package com.baidaidai.rootless_store.data.plugin.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PluginStatusDao {

    // Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPluginStatus(pluginStatusEntity: PluginStatusEntity)

    // Read
    @Query("SELECT * FROM pluginStatus WHERE pluginID = :pluginId LIMIT 1")
    suspend fun findPluginStatusById(pluginId: String): PluginStatusEntity?

    @Query("SELECT * FROM pluginStatus WHERE pluginID = :pluginId LIMIT 1")
    fun observePluginStatusById(pluginId: String): Flow<PluginStatusEntity?>

    @Query(value = "SELECT * FROM pluginStatus")
    fun observePluginStatuses(): Flow<List<PluginStatusEntity>>

    @Query("SELECT COUNT(*) FROM pluginStatus WHERE enabled = 1")
    suspend fun getEnabledPluginCount(): Int

    // Update
    @Query("UPDATE pluginStatus SET enabled = :isEnabled WHERE pluginID = :pluginId")
    suspend fun updatePluginEnabled(pluginId: String, isEnabled: Boolean)

    @Query("UPDATE pluginStatus SET enabled = :isEnabled WHERE enabled != :isEnabled")
    suspend fun updateAllPluginsEnabled(isEnabled: Boolean)

    // Delete
    @Query("DELETE FROM pluginStatus WHERE pluginID = :pluginId")
    suspend fun deletePluginStatusById(pluginId: String)
}
