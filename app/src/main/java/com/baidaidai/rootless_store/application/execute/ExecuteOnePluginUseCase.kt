package com.baidaidai.rootless_store.application.execute

import com.baidaidai.rootless_store.data.execute.repository.PluginExecuteRepositoryImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
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
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val executePluginWithoutEnvironmentByShizukuUseCase: ExecutePluginWithoutEnvironmentByShizukuUseCase,
    val storeStatusRepositoryImpl: StoreStatusRepositoryImpl,
    val settingPreferenceRepositoryImpl: SettingPreferenceRepositoryImpl
) {
    suspend operator fun invoke(
        pluginID: String
    ): Flow<ExecuteResult> {

        val pluginManifestRoom = pluginRepositoryImpl.getOnePluginInfo(pluginID)!!
        val shouldUseShizuku = judgeShouldUseShizuku()
        val enableMonitor = settingPreferenceRepositoryImpl.getEnableNotifyPluginStatus().first()

        // Judge if needs use Shizuku
        return if (shouldUseShizuku && pluginManifestRoom.bypassEnvironment) {
            executePluginWithoutEnvironmentByShizukuUseCase(pluginID)
        } else if (shouldUseShizuku) {
            pluginExecuteRepositoryImpl.executeOnePluginByShizuku(pluginManifestRoom,enableMonitor)
        } else {
            pluginExecuteRepositoryImpl.executeOnePlugin(pluginManifestRoom,enableMonitor)
        }
    }

    private suspend fun judgeShouldUseShizuku(): Boolean{

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


        return hosterOverallStatus == HosterOverallStatus.ADB && (!enableChooser || selectedExecuteContext == HosterOverallStatus.ADB)
    }
}
