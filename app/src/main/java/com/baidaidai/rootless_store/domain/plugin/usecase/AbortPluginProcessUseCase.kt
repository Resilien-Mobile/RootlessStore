package com.baidaidai.rootless_store.domain.plugin.usecase

import com.baidaidai.rootless_store.data.execute.repository.PluginExecuteRepositoryImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginCoreRepositoryImpl
import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AbortPluginProcessUseCase @Inject constructor(
    private val pluginCoreRepositoryImpl: PluginCoreRepositoryImpl,
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

        val pluginManifestRoom = pluginCoreRepositoryImpl.getOnePluginInfo(pluginID)!!

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