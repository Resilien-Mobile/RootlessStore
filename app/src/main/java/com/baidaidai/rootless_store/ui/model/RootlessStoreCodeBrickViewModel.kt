package com.baidaidai.rootless_store.ui.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.application.codebrick.AddCodeBrickUseCase
import com.baidaidai.rootless_store.application.codebrick.AddJsonCodeBrickUseCase
import com.baidaidai.rootless_store.application.codebrick.ConvertCodeBrickToPluginUseCase
import com.baidaidai.rootless_store.application.codebrick.DeleteCodeBrickUseCase
import com.baidaidai.rootless_store.application.codebrick.ExecuteCodeBrickUseCase
import com.baidaidai.rootless_store.application.codebrick.GetAllCodeBrickUseCase
import com.baidaidai.rootless_store.application.codebrick.UpdateCodeBrickUseCase
import com.baidaidai.rootless_store.domain.codebrick.error.CodeBrickError
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
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

data class CodeBrickScreenUIState(
    val brickEditorCanShow: Boolean = false,
    val executeResultCanShow: Boolean = false,
    val brickSettingCanShow: Boolean = false,
    val buttonMenuExpandStatus: Boolean = false,
    val executeResultContent: List<String> = emptyList(),
    val handlingCodeBrickConfig: CodeBrickConfig = CodeBrickConfig(unixTimeStamp = 1L,"", HosterOverallStatus.LIMITED,"")
)

@HiltViewModel
class RootlessStoreCodeBrickViewModel @Inject constructor(
    private val addCodeBrickUseCase: AddCodeBrickUseCase,
    private val getAllCodeBrickUseCase: GetAllCodeBrickUseCase,
    private val executeCodeBrickUseCase: ExecuteCodeBrickUseCase,
    private val deleteCodeBrickUseCase: DeleteCodeBrickUseCase,
    private val updateCodeBrickUseCase: UpdateCodeBrickUseCase,
    private val addJsonCodeBrickUseCase: AddJsonCodeBrickUseCase,
    private val convertCodeBrickToPluginUseCase: ConvertCodeBrickToPluginUseCase
): ViewModel() {

    private val _codeBrickScreenUIState = MutableStateFlow(CodeBrickScreenUIState())
    val codeBrickScreenUIState = _codeBrickScreenUIState.asStateFlow()

    private val _codeBrickEvent = MutableSharedFlow<CodeBrickError?>()
    val codeBrickEvent = _codeBrickEvent.asSharedFlow()

    val codeBrickConfigList = getAllCodeBrickUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = emptyList()
        )

    fun changeEditorShowStatus(
        showStatus: Boolean = false
    ){
        _codeBrickScreenUIState.update {
            it.copy(
                brickEditorCanShow = showStatus
            )
        }
    }
    fun openSettingShowStatus(
        codeBrickConfig: CodeBrickConfig,
        settingShowStatus: Boolean = true,
    ){
        _codeBrickScreenUIState.update {
            it.copy(
                brickSettingCanShow = settingShowStatus,
                handlingCodeBrickConfig = codeBrickConfig
            )
        }
    }
    fun closeSettingShowStatus(
        settingShowStatus: Boolean = false,
    ){
        _codeBrickScreenUIState.update {
            it.copy(
                brickSettingCanShow = settingShowStatus,
            )
        }
    }
    fun changeResultShowStatus(
        showStatus: Boolean = false
    ){
        _codeBrickScreenUIState.update {
            it.copy(
                executeResultCanShow = showStatus
            )
        }
    }
    fun changeButtonMenuStatus(
        showStatus: Boolean = false
    ){
        _codeBrickScreenUIState.update {
            it.copy(
                buttonMenuExpandStatus = showStatus
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
    fun createCodeBrick(
        codeBrickTitle: String,
        codeBrickContent: String,
        codeBrickContext: HosterOverallStatus,
        bindTileIndex: Int?
    ){
        viewModelScope.launch {
            addCodeBrickUseCase(codeBrickTitle,codeBrickContent,codeBrickContext,bindTileIndex)
        }
    }

    fun createCodeBrickByJson(){
        viewModelScope.launch {
            val codeBrickError = addJsonCodeBrickUseCase()
            if (codeBrickError != null){
                _codeBrickEvent.emit(codeBrickError)
            }
        }
    }

    // Update
    fun updateCodeBrick(
        codeBrickTitle: String,
        codeBrickContent: String,
        codeBrickContext: HosterOverallStatus,
        bindTileIndex: Int?,
        oldCodeBrickConfig: CodeBrickConfig
    ){
        viewModelScope.launch {
            updateCodeBrickUseCase(
                codeBrickTitle = codeBrickTitle,
                codeBrickContent = codeBrickContent,
                codeBrickContext = codeBrickContext,
                bindTileIndex = bindTileIndex,
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
            Log.d("codeBrickScreenUIState.executeResultContent", codeBrickScreenUIState.value.executeResultContent.toString())

            // Clean Up
            _codeBrickScreenUIState.update { codeBrickScreenUIState ->
                // Clean the cache
                val voidUIState = codeBrickScreenUIState.copy(
                    executeResultContent = emptyList()
                )
                // Fill the Void Content
                voidUIState.copy(
                    executeResultContent = voidUIState.executeResultContent + "Void"
                )
            }

            changeResultShowStatus(true)

            executeCodeBrickUseCase(codeBrickConfig).collect { shellResult ->
                _codeBrickScreenUIState.update { codeBrickScreenUIState ->

                    // Clean the Void-Chars cache
                    val voidUIState = codeBrickScreenUIState.copy(
                        executeResultContent = codeBrickScreenUIState.executeResultContent - "Void"
                    )

                    // Add results
                    voidUIState.copy(
                        executeResultContent = voidUIState.executeResultContent + shellResult.content
                    )
                }
            }

        }
    }
}
