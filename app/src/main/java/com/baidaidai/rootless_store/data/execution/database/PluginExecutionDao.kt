package com.baidaidai.rootless_store.data.execution.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baidaidai.rootless_store.domain.plugin.model.PluginState
import kotlinx.coroutines.flow.Flow

@Dao
interface PluginExecutionDao {
    /**
     * CRUD
     */

    // Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPluginExecution(pluginExecutionEntity: PluginExecutionEntity)

    // Update
    @Query("UPDATE PluginExecuteStatusEntry SET executeStatus = :executionState WHERE pluginID = :pluginId")
    suspend fun updatePluginExecutionStateByPluginId(pluginId: String, executionState: PluginState)

    // Read
    @Query("SELECT executeStatus FROM PluginExecuteStatusEntry WHERE pluginID = :pluginId LIMIT 1")
    fun observePluginExecutionStateByPluginId(pluginId: String): Flow<PluginState?>

    @Query("SELECT executePID FROM PluginExecuteStatusEntry WHERE pluginID = :pluginId LIMIT 1")
    suspend fun findPluginExecutionPidByPluginId(pluginId: String): Int?

    @Query("SELECT * FROM PluginExecuteStatusEntry")
    suspend fun listPluginExecutions(): List<PluginExecutionEntity>

    // Delete
    @Query("DELETE FROM PluginExecuteStatusEntry WHERE pluginID = :pluginId")
    suspend fun deletePluginExecutionByPluginId(pluginId: String)

    @Query("DELETE FROM PluginExecuteStatusEntry")
    suspend fun deleteAllPluginExecutions()

}
