package com.baidaidai.rootless_store.application.plugin

import android.net.Uri
import com.baidaidai.rootless_store.data.execution.repository.PluginExecutionRepositoryImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AbortPluginProcessUseCase @Inject constructor(
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val pluginExecutionRepositoryImpl: PluginExecutionRepositoryImpl,
    private val storeStatusRepositoryImpl: StoreStatusRepositoryImpl
) {
    suspend operator fun invoke(pluginManifestRoom: PluginManifestRoom) {
        val hosterOverallStatus = storeStatusRepositoryImpl.observeOverallStatus().first()
        val enableChooser = storeStatusRepositoryImpl
            .observeEnableChooserPreference()
            .first()
        val selectedExecutionContext = if (enableChooser) {
            storeStatusRepositoryImpl
                .observeExecutionContextPreference()
                .first()
        } else {
            null
        }

        val shouldUseShizuku =
            hosterOverallStatus == HosterOverallStatus.ADB &&
                    (!enableChooser || selectedExecutionContext == HosterOverallStatus.ADB)

        if (shouldUseShizuku) {
            pluginExecutionRepositoryImpl.abortPluginProcessByShizuku(pluginManifestRoom)
        }else{
            pluginExecutionRepositoryImpl.abortPluginProcess(pluginManifestRoom)
        }
    }

    suspend operator fun invoke(pluginId: String) {

        val pluginManifestRoom = pluginRepositoryImpl.findPluginInfo(pluginId)!!

        val hosterOverallStatus = storeStatusRepositoryImpl.observeOverallStatus().first()
        val enableChooser = storeStatusRepositoryImpl
            .observeEnableChooserPreference()
            .first()
        val selectedExecutionContext = if (enableChooser) {
            storeStatusRepositoryImpl
                .observeExecutionContextPreference()
                .first()
        } else {
            null
        }

        val shouldUseShizuku =
            hosterOverallStatus == HosterOverallStatus.ADB &&
                    (!enableChooser || selectedExecutionContext == HosterOverallStatus.ADB)

        if (shouldUseShizuku) {
            pluginExecutionRepositoryImpl.abortPluginProcessByShizuku(pluginManifestRoom)
        }else{
            pluginExecutionRepositoryImpl.abortPluginProcess(pluginManifestRoom)
        }
    }
}
