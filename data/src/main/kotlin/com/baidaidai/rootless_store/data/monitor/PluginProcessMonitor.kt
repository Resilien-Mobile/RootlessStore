package com.baidaidai.rootless_store.data.monitor

import com.baidaidai.rootless_store.domain.execution.error.ExecutionError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PluginProcessMonitor @Inject constructor() {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val unexpectedExitNotificationPoster = MutableSharedFlow<ExecutionError>()

    operator fun invoke(process: Process){
        startMonitoring(process)
    }

    operator fun invoke(exitCode: Int){
        coroutineScope.launch {
            if (exitCode != 0){
                unexpectedExitNotificationPoster.emit(ExecutionError(
                    errorMessage = "Plugin Crashed",
                    errorCause = exitCode.toString()
                ))
            }
        }
    }

    private fun startMonitoring(
        process: Process
    ){
        coroutineScope.launch {
            val exitCode = process.waitFor()
            if (exitCode != 0) {
                unexpectedExitNotificationPoster.emit(ExecutionError(
                    errorMessage = "Plugin Crashed",
                    errorCause = exitCode.toString()
                ))
            }
        }
    }

}
