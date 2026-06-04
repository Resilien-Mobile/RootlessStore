package com.baidaidai.rootless_store.data.execute.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baidaidai.rootless_store.domain.plugin.model.PluginState
import com.baidaidai.rootless_store.domain.status.model.PluginStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface PluginExecuteStatusDAO {
    /**
     * CURD
     */

    // Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOnePluginExecuteStatus(pluginExecuteStatusEntry: PluginExecuteStatusEntry)

    // Update
    @Query("UPDATE PluginExecuteStatusEntry SET executeStatus = :executeStatus WHERE pluginID = :pluginID")
    suspend fun updatePluginExecuteStatusByPluginID(pluginID: String, executeStatus: PluginState)

    // Read
    @Query("SELECT executeStatus FROM PluginExecuteStatusEntry WHERE pluginID = :pluginID LIMIT 1")
    fun getPluginExecuteStatusByPluginID(pluginID: String): Flow<PluginState?>

    @Query("SELECT executePID FROM PluginExecuteStatusEntry WHERE pluginID = :pluginID LIMIT 1")
    suspend fun getPluginExecutePIDByPluginID(pluginID: String): Int?

    @Query("SELECT * FROM PluginExecuteStatusEntry")
    suspend fun getAllExecutingPluginEntity(): List<PluginExecuteStatusEntry>

    // Delete
    @Query("DELETE FROM PluginExecuteStatusEntry WHERE pluginID = :pluginID")
    suspend fun deleteExecuteRecordByPluginID(pluginID: String)

    @Query("DELETE FROM PluginExecuteStatusEntry")
    suspend fun deleteAllExecuteRecord()

}
