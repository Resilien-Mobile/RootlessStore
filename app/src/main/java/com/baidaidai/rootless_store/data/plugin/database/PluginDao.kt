package com.baidaidai.rootless_store.data.plugin.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import kotlinx.coroutines.flow.Flow

@Dao
interface PluginDao {
    /**
     * CRUD
     */

    // Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlugin(pluginInfoEntity: PluginInfoEntity)

    // Update
    @Query("UPDATE pluginInfo SET enabled = :isEnabled WHERE pluginID = :pluginId")
    suspend fun updatePluginEnabled(pluginId: String, isEnabled: Boolean)

    @Query("UPDATE pluginInfo SET enabled = 0 WHERE enabled = 1")
    suspend fun disableAllPlugins()

    // Read
    @Query("SELECT * FROM pluginInfo WHERE pluginID = :pluginId LIMIT 1")
    suspend fun findPluginById(pluginId: String): PluginManifestRoom?

    @Query(value = "SELECT * FROM pluginInfo")
    fun observePlugins(): Flow<List<PluginManifestRoom>>

    @Query("SELECT COUNT(*) FROM pluginInfo")
    fun observePluginCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM pluginInfo")
    suspend fun getPluginCount(): Int

    @Query("SELECT COUNT(*) FROM pluginInfo WHERE enabled = 1")
    suspend fun getEnabledPluginCount(): Int

    // Delete
    @Query("DELETE FROM pluginInfo WHERE pluginID = :pluginId")
    suspend fun deletePluginById(pluginId: String)

    /**
     * Other methods, such as update、disable、configuration change,
     * Will add in no-longer future
     */
}
