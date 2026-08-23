package com.baidaidai.rootless_store.application.execute

import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.execute.model.ExecuteResult
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ExecutePluginUseCase @Inject constructor(
    private val executePluginByShizukuUseCase: ExecutePluginByShizukuUseCase,
    private val executePluginByAppShellUseCase: ExecutePluginByAppShellUseCase,
    val storeStatusRepositoryImpl: StoreStatusRepositoryImpl,
) {
    suspend operator fun invoke(
        pluginID: String
    ): Flow<ExecuteResult> {

        val shouldUseShizuku = judgeShouldUseShizuku()

        // Judge if needs use Shizuku
        return if (shouldUseShizuku) {
            executePluginByShizukuUseCase(pluginID)
        } else {
            executePluginByAppShellUseCase(pluginID)
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
