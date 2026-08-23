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
        pluginId: String
    ): Flow<ExecutionResult> {

        val shouldUseShizuku = judgeShouldUseShizuku()

        // Judge if needs use Shizuku
        return if (shouldUseShizuku) {
            executePluginByShizukuUseCase(pluginId)
        } else {
            executePluginByAppShellUseCase(pluginId)
        }
    }

    private suspend fun judgeShouldUseShizuku(): Boolean{

        val hosterOverallStatus = storeStatusRepositoryImpl.observeOverallStatus().first()
        val isExecutionContextChooserEnabled = storeStatusRepositoryImpl
            .observeExecutionContextChooserEnabled()
            .first()
        val selectedExecutionContext = if (isExecutionContextChooserEnabled) {
            storeStatusRepositoryImpl
                .observeExecutionContextPreference()
                .first()
        } else {
            null
        }


        return hosterOverallStatus == HosterOverallStatus.ADB && (!isExecutionContextChooserEnabled || selectedExecutionContext == HosterOverallStatus.ADB)
    }
}
