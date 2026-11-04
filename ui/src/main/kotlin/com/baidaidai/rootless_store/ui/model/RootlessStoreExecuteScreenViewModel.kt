package com.baidaidai.rootless_store.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.domain.execution.model.ExecutionResult
import com.baidaidai.rootless_store.application.execute.ExecutePluginUseCase
import com.baidaidai.rootless_store.application.plugin.AbortPluginProcessUseCase
import com.baidaidai.rootless_store.domain.execution.error.ExecutionError
import dagger.hilt.android.lifecycle.HiltViewModel
import com.github.michaelbull.result.onErr
import com.github.michaelbull.result.onOk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootlessStoreExecuteScreenViewModel @Inject constructor(
    private val executePluginUseCase: ExecutePluginUseCase,
    private val abortPluginProcessUseCase: AbortPluginProcessUseCase,
): ViewModel() {

    private val _executionLog = MutableStateFlow<List<ExecutionResult>>(emptyList())
    val executionLog = _executionLog.asStateFlow()

    private val _executionError = MutableStateFlow<ExecutionError?>(null)
    val executionError = _executionError.asStateFlow()

    fun executePlugin(pluginId: String){
        _executionLog.value = emptyList()
        viewModelScope.launch {
            executePluginUseCase(pluginId)
                .onOk { executionFlow ->
                    executionFlow.collect { executionResult ->
                        _executionLog.value += executionResult
                    }
                }
                .onErr { executionError ->
                    _executionError.value = executionError
                }
        }
    }

    fun clearExecutionError() {
        _executionError.value = null
    }

    fun abortPluginProcess(pluginId: String) {
        viewModelScope.launch{
            abortPluginProcessUseCase(pluginId)
        }
    }

    fun exportExecutionLog(executionLog: List<ExecutionResult> = _executionLog.value): String{
        return executionLog.joinToString(separator = "\n") { it.output }
    }

}
