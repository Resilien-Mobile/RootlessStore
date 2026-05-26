package com.baidaidai.rootless_store.domain.execute.usecase

import com.baidaidai.rootless_store.data.execute.repository.PluginExecuteRepositoryImpl
import com.baidaidai.rootless_store.data.setting.repository.SettingPreferenceRepositoryImpl
import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.execute.model.ExecuteResult
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ExecuteOnePluginUseCase @Inject constructor(
    private val pluginExecuteRepositoryImpl: PluginExecuteRepositoryImpl,
    val storeStatusRepositoryImpl: StoreStatusRepositoryImpl,
    val settingPreferenceRepositoryImpl: SettingPreferenceRepositoryImpl
) {
    suspend operator fun invoke(
        pluginManifestRoom: PluginManifestRoom
    ): Flow<ExecuteResult> {
        val hosterOverallStatus = storeStatusRepositoryImpl.getOverallStatus()
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

        val enableMonitor = settingPreferenceRepositoryImpl.getEnableNotifyPluginStatus().first()

        // Judge if needs use shizuku
        return if (shouldUseShizuku) {
            pluginExecuteRepositoryImpl.executeOnePluginByShizuku(pluginManifestRoom,enableMonitor)
        } else {
            pluginExecuteRepositoryImpl.executeOnePlugin(pluginManifestRoom,enableMonitor)
        }
    }
}