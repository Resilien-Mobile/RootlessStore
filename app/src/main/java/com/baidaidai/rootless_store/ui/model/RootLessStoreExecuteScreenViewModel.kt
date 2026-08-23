package com.baidaidai.rootless_store.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.domain.execution.model.ExecutionResult
import com.baidaidai.rootless_store.application.execute.ExecutePluginUseCase
import com.baidaidai.rootless_store.application.plugin.AbortPluginProcessUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootLessStoreExecuteScreenViewModel @Inject constructor(
    private val executePluginUseCase: ExecutePluginUseCase,
    private val abortPluginProcessUseCase: AbortPluginProcessUseCase,
): ViewModel() {

    private val _executionLog = MutableStateFlow<List<ExecutionResult>>(emptyList())
    val executionLog = _executionLog.asStateFlow()

    fun executePlugin(pluginID: String){
        _executionLog.value = emptyList()
        viewModelScope.launch {
            executePluginUseCase(pluginID)
                .collect {
                _executionLog.value += it
                }
        }
    }

    fun abortPluginProcess(pluginID: String) {
        viewModelScope.launch{
            abortPluginProcessUseCase(pluginID)
        }
    }

    fun exportExecutionLog(executionLog: List<ExecutionResult> = _executionLog.value): String{
        return executionLog.joinToString(separator = "\n") { it.content }
    }

}
