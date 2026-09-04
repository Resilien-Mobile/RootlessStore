package com.baidaidai.rootless_store.application.execute

import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.execution.model.ExecutionResult
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ExecutePluginUseCase @Inject constructor(
    private val executePluginByShizukuUseCase: com.baidaidai.rootless_store.application.execute.ExecutePluginByShizukuUseCase,
    private val executePluginByAppShellUseCase: com.baidaidai.rootless_store.application.execute.ExecutePluginByAppShellUseCase,
    private val storeStatusRepositoryImpl: StoreStatusRepositoryImpl,
) {
    suspend operator fun invoke(
        pluginId: String
    ): Flow<ExecutionResult> {

        val shouldUseShizuku = shouldUseShizuku()

        // Judge if needs use Shizuku
        return if (shouldUseShizuku) {
            executePluginByShizukuUseCase(pluginId)
        } else {
            executePluginByAppShellUseCase(pluginId)
        }
    }

    private suspend fun shouldUseShizuku(): Boolean{

        val availableExecutionContext = storeStatusRepositoryImpl.observeAvailableExecutionContext().first()
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


        return availableExecutionContext == ExecutionContext.ADB && (!isExecutionContextChooserEnabled || selectedExecutionContext == ExecutionContext.ADB)
    }
}
