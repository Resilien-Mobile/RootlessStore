package com.baidaidai.rootless_store.ui.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.application.codebrick.AddCodeBrickUseCase
import com.baidaidai.rootless_store.application.codebrick.AddCodeBrickFromClipboardUseCase
import com.baidaidai.rootless_store.application.codebrick.ConvertCodeBrickToPluginUseCase
import com.baidaidai.rootless_store.application.codebrick.DeleteCodeBrickUseCase
import com.baidaidai.rootless_store.application.codebrick.ExecuteCodeBrickUseCase
import com.baidaidai.rootless_store.application.codebrick.ObserveCodeBricksUseCase
import com.baidaidai.rootless_store.application.codebrick.UpdateCodeBrickUseCase
import com.baidaidai.rootless_store.domain.codebrick.error.CodeBrickError
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
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

data class CodeBrickScreenUiState(
    val isBrickEditorVisible: Boolean = false,
    val isExecutionResultVisible: Boolean = false,
    val isBrickSettingsVisible: Boolean = false,
    val isButtonMenuExpanded: Boolean = false,
    val executionOutputLines: List<String> = emptyList(),
    val selectedCodeBrick: CodeBrickConfig = CodeBrickConfig(unixTimestamp = 1L,"", ExecutionContext.LIMITED,"")
)

@HiltViewModel
class RootlessStoreCodeBrickViewModel @Inject constructor(
    private val addCodeBrickUseCase: AddCodeBrickUseCase,
    private val observeCodeBricksUseCase: ObserveCodeBricksUseCase,
    private val executeCodeBrickUseCase: ExecuteCodeBrickUseCase,
    private val deleteCodeBrickUseCase: DeleteCodeBrickUseCase,
    private val updateCodeBrickUseCase: UpdateCodeBrickUseCase,
    private val addCodeBrickFromClipboardUseCase: AddCodeBrickFromClipboardUseCase,
    private val convertCodeBrickToPluginUseCase: ConvertCodeBrickToPluginUseCase
): ViewModel() {

    private val _codeBrickScreenUiState = MutableStateFlow(CodeBrickScreenUiState())
    val codeBrickScreenUiState = _codeBrickScreenUiState.asStateFlow()

    private val _codeBrickEvent = MutableSharedFlow<CodeBrickError?>()
    val codeBrickEvent = _codeBrickEvent.asSharedFlow()

    val codeBricks = observeCodeBricksUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = emptyList()
        )

    fun setBrickEditorVisible(
        isVisible: Boolean = false
    ){
        _codeBrickScreenUiState.update {
            it.copy(
                isBrickEditorVisible = isVisible
            )
        }
    }
    fun showBrickSettings(
        codeBrickConfig: CodeBrickConfig
    ){
        _codeBrickScreenUiState.update {
            it.copy(
                isBrickSettingsVisible = true,
                selectedCodeBrick = codeBrickConfig
            )
        }
    }
    fun hideBrickSettings(){
        _codeBrickScreenUiState.update {
            it.copy(
                isBrickSettingsVisible = false,
            )
        }
    }
    fun setExecutionResultVisible(
        isVisible: Boolean = false
    ){
        _codeBrickScreenUiState.update {
            it.copy(
                isExecutionResultVisible = isVisible
            )
        }
    }
    fun setButtonMenuExpanded(
        isExpanded: Boolean = false
    ){
        _codeBrickScreenUiState.update {
            it.copy(
                isButtonMenuExpanded = isExpanded
            )
        }
    }

    fun convertCodeBrickToPlugin(
        codeBrickConfig: CodeBrickConfig
    ){
        viewModelScope.launch {
            convertCodeBrickToPluginUseCase(codeBrickConfig)
        }
    }

    // CUDE

    // Create
    fun addCodeBrick(
        codeBrickTitle: String,
        codeBrickContent: String,
        codeBrickContext: ExecutionContext,
        boundTileIndex: Int?
    ){
        viewModelScope.launch {
            addCodeBrickUseCase(codeBrickTitle,codeBrickContent,codeBrickContext,boundTileIndex)
        }
    }

    fun addCodeBrickFromClipboard(){
        viewModelScope.launch {
            val codeBrickError = addCodeBrickFromClipboardUseCase()
            if (codeBrickError != null){
                _codeBrickEvent.emit(codeBrickError)
            }
        }
    }

    // Update
    fun updateCodeBrick(
        codeBrickTitle: String,
        codeBrickContent: String,
        codeBrickContext: ExecutionContext,
        boundTileIndex: Int?,
        oldCodeBrickConfig: CodeBrickConfig
    ){
        viewModelScope.launch {
            updateCodeBrickUseCase(
                codeBrickTitle = codeBrickTitle,
                codeBrickContent = codeBrickContent,
                codeBrickContext = codeBrickContext,
                boundTileIndex = boundTileIndex,
                oldCodeBrickConfig = oldCodeBrickConfig
            )
        }
    }

    // Delete
    fun deleteCodeBrick(
        codeBrickConfig: CodeBrickConfig
    ){
        viewModelScope.launch {
            deleteCodeBrickUseCase(codeBrickConfig)
        }
    }

    // Execute
    fun executeCodeBrick(
        codeBrickConfig: CodeBrickConfig
    ){
        viewModelScope.launch {
            Log.d("CodeBrickExecution", codeBrickScreenUiState.value.executionOutputLines.toString())

            // Clean Up
            _codeBrickScreenUiState.update { codeBrickScreenUiState ->
                // Clean the cache
                val clearedUiState = codeBrickScreenUiState.copy(
                    executionOutputLines = emptyList()
                )
                // Fill the Void Content
                clearedUiState.copy(
                    executionOutputLines = clearedUiState.executionOutputLines + "Void"
                )
            }

            setExecutionResultVisible(true)

            executeCodeBrickUseCase(codeBrickConfig).collect { shellResult ->
                _codeBrickScreenUiState.update { codeBrickScreenUiState ->

                    // Clean the Void-Chars cache
                    val clearedUiState = codeBrickScreenUiState.copy(
                        executionOutputLines = codeBrickScreenUiState.executionOutputLines - "Void"
                    )

                    // Add results
                    clearedUiState.copy(
                        executionOutputLines = clearedUiState.executionOutputLines + shellResult.content
                    )
                }
            }

        }
    }
}
