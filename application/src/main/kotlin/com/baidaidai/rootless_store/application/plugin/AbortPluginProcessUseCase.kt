package com.baidaidai.rootless_store.application.plugin

import com.baidaidai.rootless_store.data.execution.repository.PluginExecutionRepositoryImpl
import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AbortPluginProcessUseCase @Inject constructor(
    private val pluginExecutionRepositoryImpl: PluginExecutionRepositoryImpl,
    private val storeStatusRepositoryImpl: StoreStatusRepositoryImpl
) {
    suspend operator fun invoke(pluginId: String) {
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

        val shouldUseShizuku =
            availableExecutionContext == ExecutionContext.ADB &&
                    (!isExecutionContextChooserEnabled || selectedExecutionContext == ExecutionContext.ADB)

        if (shouldUseShizuku) {
            pluginExecutionRepositoryImpl.abortPluginProcessByShizuku(pluginId)
        } else {
            pluginExecutionRepositoryImpl.abortPluginProcess(pluginId)
        }
    }
}
