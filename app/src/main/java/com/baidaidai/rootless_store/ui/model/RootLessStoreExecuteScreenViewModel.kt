package com.baidaidai.rootless_store.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.domain.execute.model.ExecuteResult
import com.baidaidai.rootless_store.domain.execute.usecase.ExecuteOnePluginUseCase
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import com.baidaidai.rootless_store.domain.plugin.usecase.AbortPluginProcessUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootLessStoreExecuteScreenViewModel @Inject constructor(
    private val executeOnePluginUseCase: ExecuteOnePluginUseCase,
    private val abortPluginProcessUseCase: AbortPluginProcessUseCase,
): ViewModel() {

    private val _executeLog = MutableStateFlow<List<ExecuteResult>>(emptyList())
    private var _currentPlugin: PluginManifestRoom? by mutableStateOf(null)
    val executeLog = _executeLog.asStateFlow()

    fun executeOnePlugin(pluginManifestRoom: PluginManifestRoom){
        _executeLog.value = emptyList()
        _currentPlugin = pluginManifestRoom
        viewModelScope.launch {
            executeOnePluginUseCase(pluginManifestRoom)
                .collect {
                    _executeLog.value += it
                }
        }
    }

    fun abortPluginProcess(pluginManifestRoom: PluginManifestRoom? = _currentPlugin) {
        if (pluginManifestRoom != null){
            viewModelScope.launch{
                abortPluginProcessUseCase(pluginManifestRoom)
                _currentPlugin = null
            }
        }
    }

    fun exportExecuteLog(executeLog: List<ExecuteResult> = _executeLog.value): String{
        return executeLog.joinToString(separator = "\n") { it.content }
    }

}