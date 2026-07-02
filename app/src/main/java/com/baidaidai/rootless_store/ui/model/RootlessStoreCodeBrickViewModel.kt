package com.baidaidai.rootless_store.ui.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.application.codebrick.AddOneCodeBrickUseCase
import com.baidaidai.rootless_store.application.codebrick.DeleteOneCodeBrickUseCase
import com.baidaidai.rootless_store.application.codebrick.ExecuteOneCodeBrickUseCase
import com.baidaidai.rootless_store.application.codebrick.GetAllCodeBrickUseCase
import com.baidaidai.rootless_store.application.codebrick.UpdateOneCodeBrickUseCase
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CodeBrickScreenUIState(
    val brickEditorCanShow: Boolean = false,
    val executeResultCanShow: Boolean = false,
    val brickSettingCanShow: Boolean = false,
    val executeResultContent: List<String> = emptyList(),
    val handlingCodeBrickConfig: CodeBrickConfig = CodeBrickConfig(unixTimeStamp = 1L,"", HosterOverallStatus.LIMITED,"")
)

@HiltViewModel
class RootlessStoreCodeBrickViewModel @Inject constructor(
    private val addOneCodeBrickUseCase: AddOneCodeBrickUseCase,
    private val getAllCodeBrickUseCase: GetAllCodeBrickUseCase,
    private val executeOneCodeBrickUseCase: ExecuteOneCodeBrickUseCase,
    private val deleteOneCodeBrickUseCase: DeleteOneCodeBrickUseCase,
    private val updateOneCodeBrickUseCase: UpdateOneCodeBrickUseCase
): ViewModel() {

    private val _codeBrickScreenUIState = MutableStateFlow(CodeBrickScreenUIState())

    val codeBrickScreenUIState = _codeBrickScreenUIState.asStateFlow()

    val codeBrickConfigList = getAllCodeBrickUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = emptyList()
        )

    private val _editorShowStatus = MutableStateFlow(false)
    val editorShowStatus = _editorShowStatus.asStateFlow()


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

    // CUDE

    // Create
    fun createOneCodeBrick(
        codeBrickTitle: String,
        codeBrickContent: String,
        codeBrickContext: HosterOverallStatus,
        bindTileIndex: Int?
    ){
        viewModelScope.launch {
            addOneCodeBrickUseCase(codeBrickTitle,codeBrickContent,codeBrickContext,bindTileIndex)
        }
    }

    // Update
    fun updateOneCodeBrick(
        codeBrickTitle: String,
        codeBrickContent: String,
        codeBrickContext: HosterOverallStatus,
        bindTileIndex: Int?,
        oldCodeBrickConfig: CodeBrickConfig
    ){
        viewModelScope.launch {
            updateOneCodeBrickUseCase(
                codeBrickTitle = codeBrickTitle,
                codeBrickContent = codeBrickContent,
                codeBrickContext = codeBrickContext,
                bindTileIndex = bindTileIndex,
                oldCodeBrickConfig = oldCodeBrickConfig
            )
        }
    }

    // Delete
    fun deleteOneCodeBrick(
        codeBrickConfig: CodeBrickConfig
    ){
        viewModelScope.launch {
            deleteOneCodeBrickUseCase(codeBrickConfig)
        }
    }

    // Execute
    fun executeOneCodeBrick(
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

            executeOneCodeBrickUseCase(codeBrickConfig).collect { shellResult ->
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
