package com.baidaidai.rootless_store.data.execution.database

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import com.baidaidai.rootless_store.domain.plugin.model.PluginState
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import javax.inject.Inject

@Entity(tableName = "PluginExecuteStatusEntry")
data class PluginExecutionEntity @Inject constructor(
    @PrimaryKey
    @ColumnInfo(name = "pluginID")
    val pluginId: String,
    @ColumnInfo(name = "executeStatus")
    val executionState: PluginState,
    @ColumnInfo(name = "executePID")
    val executionPid: Int,
    @ColumnInfo(name = "executeContext", defaultValue = "'LIMITED'")
    val executionContext: HosterOverallStatus = HosterOverallStatus.LIMITED
){
    companion object{
        fun fromPluginManifest(pluginManifest: PluginManifest, executionPid: Int): PluginExecutionEntity{
            return PluginExecutionEntity(
                pluginId = pluginManifest.pluginId,
                executionState = PluginState.Great,
                executionPid = executionPid
            )
        }
    }
}
