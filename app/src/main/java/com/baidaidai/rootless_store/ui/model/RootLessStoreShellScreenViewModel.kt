package com.baidaidai.rootless_store.ui.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.domain.shell.model.ShellCommandContainer
import com.baidaidai.rootless_store.domain.shell.model.ShellResult
import com.baidaidai.rootless_store.domain.shell.usecase.ObserveAdbShellStatusUseCase
import com.baidaidai.rootless_store.domain.shell.usecase.GetRootShellStatusUseCase
import com.baidaidai.rootless_store.domain.shell.usecase.ObserveShellContextPreferencesUseCase
import com.baidaidai.rootless_store.application.shell.RunCommandUseCase
import com.baidaidai.rootless_store.domain.shell.usecase.SetShellDirectoryJumpEnabledUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootLessStoreShellScreenViewModel @Inject constructor(
    private val runCommandUseCase: RunCommandUseCase,
    private val getRootShellStatusUseCase: GetRootShellStatusUseCase,
    private val observeAdbShellStatusUseCase: ObserveAdbShellStatusUseCase,
    observeShellContextPreferencesUseCase: ObserveShellContextPreferencesUseCase,
    private val setShellDirectoryJumpEnabledUseCase: SetShellDirectoryJumpEnabledUseCase
) : ViewModel(){

    private var _shellOutputList = MutableStateFlow(emptyList<ShellResult>())
    private val _rootShellStatus = MutableStateFlow(getRootShellStatusUseCase())
    private var _lastCommandContent = MutableStateFlow("")
    val shellContextPreferences = observeShellContextPreferencesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = observeShellContextPreferencesUseCase.defaultPreferences
        )
    val shellOutputList = _shellOutputList.asStateFlow()
    val rootShellStatus = _rootShellStatus.asStateFlow()
    val adbShellStatus = observeAdbShellStatusUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )
    val lastCommandContent = _lastCommandContent.asStateFlow()

    fun runCommand(shellCommandContainer: ShellCommandContainer){
        viewModelScope.launch {
            val shellOutput = runCommandUseCase(shellCommandContainer)
            shellOutput.collect { shellResult ->

                Log.d("ShellViewModel","shellResult.command: ${shellResult.command}")
                Log.d("ShellViewModel","lastCommandContent: ${lastCommandContent.value}")
                Log.d("ShellViewModel","lastCommandContent == shellResult.command: ${lastCommandContent.value == shellResult.command}")

                if (_lastCommandContent.value != shellResult.command && shellResult.command != null){

                    _lastCommandContent.value = shellResult.command
                    _shellOutputList.value += shellResult

                }else if(_lastCommandContent.value == shellResult.command){

                    _shellOutputList.value += shellResult.copy(command = null)

                }



            }
        }
        _lastCommandContent.value = ""
    }

    fun cleanShellOutputList() {
        _shellOutputList.value =  emptyList()
    }

    fun setDirectoryJumpEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            setShellDirectoryJumpEnabledUseCase(isEnabled)
        }
    }

}
