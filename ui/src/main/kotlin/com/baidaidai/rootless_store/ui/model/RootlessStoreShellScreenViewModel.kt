package com.baidaidai.rootless_store.ui.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.application.assistant.FindAssistantModelFileUseCase
import com.baidaidai.rootless_store.application.assistant.InitializeAssistantEngineUseCase
import com.baidaidai.rootless_store.application.assistant.ObserveAssistantEngineStatusUseCase
import com.baidaidai.rootless_store.application.assistant.ResolveMaskedResponseUseCase
import com.baidaidai.rootless_store.application.shell.ExecuteShellCommandUseCase
import com.baidaidai.rootless_store.application.shell.GetRootShellAvailabilityUseCase
import com.baidaidai.rootless_store.application.shell.ObserveAdbShellAvailabilityUseCase
import com.baidaidai.rootless_store.application.shell.ObserveShellContextPreferencesUseCase
import com.baidaidai.rootless_store.application.shell.SetShellDirectoryJumpEnabledUseCase
import com.baidaidai.rootless_store.domain.assistant.error.AssistantError
import com.baidaidai.rootless_store.domain.assistant.model.AssistantEngineStatus
import com.baidaidai.rootless_store.domain.assistant.model.AssistantJudgeStatus
import com.baidaidai.rootless_store.domain.shell.model.ShellCommand
import com.baidaidai.rootless_store.domain.shell.model.ShellResult
import com.baidaidai.rootless_store.ui.R
import com.github.michaelbull.result.onErr
import com.github.michaelbull.result.onOk
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RootlessStoreShellScreenUiState(
    val isEngineBoardVisible: Boolean = false,
    val isTextFieldEnabled: Boolean = true
)

@HiltViewModel
class RootlessStoreShellScreenViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val executeShellCommandUseCase: ExecuteShellCommandUseCase,
    private val getRootShellAvailabilityUseCase: GetRootShellAvailabilityUseCase,
    private val observeAdbShellAvailabilityUseCase: ObserveAdbShellAvailabilityUseCase,
    private val observeShellContextPreferencesUseCase: ObserveShellContextPreferencesUseCase,
    private val setShellDirectoryJumpEnabledUseCase: SetShellDirectoryJumpEnabledUseCase,
    private val resolveMaskedResponseUseCase: ResolveMaskedResponseUseCase,
    private val initializeAssistantEngineUseCase: InitializeAssistantEngineUseCase,
    private val observeAssistantEngineStatusUseCase: ObserveAssistantEngineStatusUseCase,
    private val findAssistantModelFileUseCase: FindAssistantModelFileUseCase
) : ViewModel(){

    private var _shellOutputs = MutableStateFlow(emptyList<ShellResult>())
    private val _isRootShellAvailable = MutableStateFlow(getRootShellAvailabilityUseCase())
    private var _lastCommandContent = MutableStateFlow("")
    private val _resolvedMaskedCommand = MutableSharedFlow<String>()
    private val _shellError = MutableSharedFlow<AssistantError?>()
    private val _shellScreenUiState = MutableStateFlow(RootlessStoreShellScreenUiState())
    val shellContextPreferences = observeShellContextPreferencesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = observeShellContextPreferencesUseCase.defaultPreferences
        )
    val shellOutputs = _shellOutputs.asStateFlow()
    val isRootShellAvailable = _isRootShellAvailable.asStateFlow()
    val isAdbShellAvailable = observeAdbShellAvailabilityUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )
    val assistantEngineStatus = observeAssistantEngineStatusUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AssistantEngineStatus.Released
        )
    val lastCommandContent = _lastCommandContent.asStateFlow()
    val resolvedMaskedCommand = _resolvedMaskedCommand.asSharedFlow()
    val shellError = _shellError.asSharedFlow()
    val shellScreenUiState = _shellScreenUiState.asStateFlow()

    fun executeCommand(shellCommand: ShellCommand){
        viewModelScope.launch {
            val shellOutput = executeShellCommandUseCase(shellCommand)
            shellOutput.collect { shellResult ->

                Log.d("ShellViewModel","shellResult.command: ${shellResult.command}")
                Log.d("ShellViewModel","lastCommandContent: ${lastCommandContent.value}")
                Log.d("ShellViewModel","lastCommandContent == shellResult.command: ${lastCommandContent.value == shellResult.command}")

                if (_lastCommandContent.value != shellResult.command && shellResult.command != null){

                    _lastCommandContent.value = shellResult.command.toString()
                    _shellOutputs.value += shellResult

                }else if(_lastCommandContent.value == shellResult.command){

                    _shellOutputs.value += shellResult.copy(command = null)

                }



            }
        }
        _lastCommandContent.value = ""
    }

    fun resolveMaskedCommand(prompt: String) {
        if (!_shellScreenUiState.value.isTextFieldEnabled) {
            return
        }

        viewModelScope.launch {
            _shellScreenUiState.update { uiState ->
                uiState.copy(isTextFieldEnabled = false)
            }

            try {
                resolveMaskedResponseUseCase(prompt)
                    .onOk { shellCommand ->
                        val shellCode = shellCommand.shellCode
                        when {
                            shellCommand.status == AssistantJudgeStatus.illegal -> {
                                _resolvedMaskedCommand.emit("")
                                _shellError.emit(
                                    AssistantError(
                                        errorMessage = context.getString(
                                            R.string.shell_screen_assistant_error_command_rejected_title
                                        ),
                                        errorCompanion = context.getString(
                                            R.string.shell_screen_assistant_error_command_rejected_description
                                        )
                                    )
                                )
                            }
                            shellCode == null -> {
                                _resolvedMaskedCommand.emit("")
                                _shellError.emit(
                                    AssistantError(
                                        errorMessage = context.getString(
                                            R.string.shell_screen_assistant_error_no_command_title
                                        ),
                                        errorCompanion = context.getString(
                                            R.string.shell_screen_assistant_error_no_command_description
                                        )
                                    )
                                )
                            }
                            else -> {
                                _resolvedMaskedCommand.emit(shellCode)
                            }
                        }
                    }
                    .onErr { llmError ->
                        _resolvedMaskedCommand.emit("")
                        Log.d("ShellScreenViewModel",llmError.errorMessage)
                        _shellError.emit(llmError)
                    }
            } finally {
                _shellScreenUiState.update { uiState ->
                    uiState.copy(isTextFieldEnabled = true)
                }
            }
        }
    }

    fun resetShellOutput() {
        _shellOutputs.value =  emptyList()
    }

    fun switchEngineBoardState() {
        _shellScreenUiState.update { uiState ->
            uiState.copy(isEngineBoardVisible = !uiState.isEngineBoardVisible)
        }
    }

    fun initializeAssistantEngine() {
        viewModelScope.launch {
            initializeAssistantEngineUseCase()
                .onErr { assistantError ->
                    _shellError.emit(assistantError)
                }
        }
    }

    fun findAssistantModelFile() {
        viewModelScope.launch {
            val assistantModelFile = findAssistantModelFileUseCase()

            if (assistantModelFile == null) {
                _shellError.emit(
                    AssistantError(
                        errorMessage = context.getString(
                            R.string.shell_screen_assistant_error_model_file_not_found_title
                        ),
                        errorCompanion = context.getString(
                            R.string.shell_screen_assistant_error_model_file_not_found_description
                        )
                    )
                )
            }
        }
    }

    fun setDirectoryJumpEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            setShellDirectoryJumpEnabledUseCase(isEnabled)
        }
    }

}
