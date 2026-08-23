package com.baidaidai.rootless_store.application.execute

import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.execution.model.ExecutionResult
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
    ): Flow<ExecutionResult> {

        val shouldUseShizuku = judgeShouldUseShizuku()

        // Judge if needs use Shizuku
        return if (shouldUseShizuku) {
            executePluginByShizukuUseCase(pluginID)
        } else {
            executePluginByAppShellUseCase(pluginID)
        }
    }

    private suspend fun judgeShouldUseShizuku(): Boolean{

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


        return hosterOverallStatus == HosterOverallStatus.ADB && (!enableChooser || selectedExecutionContext == HosterOverallStatus.ADB)
    }
}
