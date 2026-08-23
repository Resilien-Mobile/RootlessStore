package com.baidaidai.rootless_store.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.domain.execute.model.ExecuteResult
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

    private val _executeLog = MutableStateFlow<List<ExecuteResult>>(emptyList())
    val executeLog = _executeLog.asStateFlow()

    fun executePlugin(pluginID: String){
        _executeLog.value = emptyList()
        viewModelScope.launch {
            executePluginUseCase(pluginID)
                .collect {
                    _executeLog.value += it
                }
        }
    }

    fun abortPluginProcess(pluginID: String) {
        viewModelScope.launch{
            abortPluginProcessUseCase(pluginID)
        }
    }

    fun exportExecuteLog(executeLog: List<ExecuteResult> = _executeLog.value): String{
        return executeLog.joinToString(separator = "\n") { it.content }
    }

}
