package com.baidaidai.rootless_store.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.baidaidai.rootless_store.ui.adaptive.RootlessStoreWindowSize
import com.baidaidai.rootless_store.ui.components.codeBrickScreen.CodeBrickEditor
import com.baidaidai.rootless_store.ui.components.codeBrickScreen.CodeBrickPreviewer
import com.baidaidai.rootless_store.ui.components.codeBrickScreen.CodeBrickResult
import com.baidaidai.rootless_store.ui.components.codeBrickScreen.CodeBrickSetting
import com.baidaidai.rootless_store.ui.model.RootlessStoreCodeBrickViewModel

@Composable
fun CodeBrickScreen(
    contentPaddingValues: PaddingValues,
    codeBrickViewModel: RootlessStoreCodeBrickViewModel = hiltViewModel<RootlessStoreCodeBrickViewModel>(),
    rootlessStoreWindowSize: RootlessStoreWindowSize,
    onBackgroundClick: ()-> Unit = {}
){

    val codeBrickConfigList by codeBrickViewModel.codeBrickConfigList.collectAsState()
    val codeBrickScreenUiState by codeBrickViewModel.codeBrickScreenUiState.collectAsState()

    // Editor show status
    if (codeBrickScreenUiState.isBrickEditorVisible){
        CodeBrickEditor(
            onDismissRequest = {
                codeBrickViewModel.setBrickEditorVisible(false)
            },
            onDismissButtonClick = {
                codeBrickViewModel.setBrickEditorVisible(false)
            },
            onConfirmButtonClick = { title, content, context, tileIndex ->
                codeBrickViewModel.createCodeBrick(
                    codeBrickTitle = title,
                    codeBrickContent = content,
                    codeBrickContext = context,
                    boundTileIndex = tileIndex
                )
                codeBrickViewModel.setBrickEditorVisible(false)
            },
        )
    } else if (codeBrickScreenUiState.isExecutionResultVisible) {
        CodeBrickResult(
            resultContent = codeBrickScreenUiState.executeResultContent,
            onDismissRequest = codeBrickViewModel::setExecutionResultVisible,
            onConfirmButtonClick = codeBrickViewModel::setExecutionResultVisible
        )
    } else if (codeBrickScreenUiState.isBrickSettingsVisible) {
        CodeBrickSetting(
            codeBrickConfig = codeBrickScreenUiState.handlingCodeBrickConfig,
            onDismissRequest = {
                codeBrickViewModel.hideBrickSettings(false)
            },
            onDismissButtonClick = {
                codeBrickViewModel.hideBrickSettings(false)
            },
            onConfirmButtonClick = { title, content, context, tileIndex, oldCodeBrickConfig ->
                codeBrickViewModel.updateCodeBrick(
                    codeBrickTitle = title,
                    codeBrickContent = content,
                    codeBrickContext = context,
                    boundTileIndex = tileIndex,
                    oldCodeBrickConfig = oldCodeBrickConfig
                )
                codeBrickViewModel.hideBrickSettings(false)
            },
            onCodeBrickToPluginButtonClick = { codeBrickConfig -> codeBrickViewModel.convertCodeBrickToPlugin(codeBrickConfig) }
        )
    }

    // Brick Reactive Style Status
    val columns = if (rootlessStoreWindowSize != RootlessStoreWindowSize.Compact){
        StaggeredGridCells.Adaptive(200.dp)
    }else{
        StaggeredGridCells.Fixed(2)
    }

    LazyVerticalStaggeredGrid(
        verticalItemSpacing = 10.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        columns = columns,
        contentPadding = PaddingValues(10.dp),
        modifier = Modifier
            .padding(contentPaddingValues)
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ){ onBackgroundClick() }
    ) {
        itemsIndexed(
            items = codeBrickConfigList
        ){ index ,codeBrickConfig ->
            CodeBrickPreviewer(
                codeBrickConfig = codeBrickConfig,
                onActionButtonClick = codeBrickViewModel::executeCodeBrick,
                onSettingButtonClick = {
                    codeBrickViewModel.showBrickSettings(
                        codeBrickConfig = codeBrickConfig,
                        isVisible = true
                    )
                },
                onDeleteButtonClick = codeBrickViewModel::deleteCodeBrick
            )
        }
    }
}
