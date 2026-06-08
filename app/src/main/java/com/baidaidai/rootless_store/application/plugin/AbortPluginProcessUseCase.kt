package com.baidaidai.rootless_store.application.plugin

import android.net.Uri
import com.baidaidai.rootless_store.data.execute.repository.PluginExecuteRepositoryImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AbortPluginProcessUseCase @Inject constructor(
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val pluginExecuteRepositoryImpl: PluginExecuteRepositoryImpl,
    private val storeStatusRepositoryImpl: StoreStatusRepositoryImpl
) {
    suspend operator fun invoke(pluginManifestRoom: PluginManifestRoom) {
        val hosterOverallStatus = storeStatusRepositoryImpl.getOverallStatus().first()
        val enableChooser = storeStatusRepositoryImpl
            .getEnableChooserPreference()
            .first()
        val selectedExecuteContext = if (enableChooser) {
            storeStatusRepositoryImpl
                .getExecuteContextPreference()
                .first()
        } else {
            null
        }

        val shouldUseShizuku =
            hosterOverallStatus == HosterOverallStatus.ADB &&
                    (!enableChooser || selectedExecuteContext == HosterOverallStatus.ADB)

        if (shouldUseShizuku) {
            pluginExecuteRepositoryImpl.abortPluginProcessByShizuku(pluginManifestRoom)
        }else{
            pluginExecuteRepositoryImpl.abortPluginProcess(pluginManifestRoom)
        }
    }

    suspend operator fun invoke(pluginID: String) {

        val pluginManifestRoom = pluginRepositoryImpl.getOnePluginInfo(pluginID)!!

        val hosterOverallStatus = storeStatusRepositoryImpl.getOverallStatus().first()
        val enableChooser = storeStatusRepositoryImpl
            .getEnableChooserPreference()
            .first()
        val selectedExecuteContext = if (enableChooser) {
            storeStatusRepositoryImpl
                .getExecuteContextPreference()
                .first()
        } else {
            null
        }

        val shouldUseShizuku =
            hosterOverallStatus == HosterOverallStatus.ADB &&
                    (!enableChooser || selectedExecuteContext == HosterOverallStatus.ADB)

        if (shouldUseShizuku) {
            pluginExecuteRepositoryImpl.abortPluginProcessByShizuku(pluginManifestRoom)
        }else{
            pluginExecuteRepositoryImpl.abortPluginProcess(pluginManifestRoom)
        }
    }
}