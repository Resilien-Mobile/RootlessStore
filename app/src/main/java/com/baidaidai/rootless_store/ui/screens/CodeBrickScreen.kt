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
import com.baidaidai.rootless_store.ui.components.codeBrickScreen.CodeBrickExecutionResultDialog
import com.baidaidai.rootless_store.ui.components.codeBrickScreen.CodeBrickSetting
import com.baidaidai.rootless_store.ui.model.RootlessStoreCodeBrickViewModel

@Composable
fun CodeBrickScreen(
    contentPaddingValues: PaddingValues,
    codeBrickViewModel: RootlessStoreCodeBrickViewModel = hiltViewModel<RootlessStoreCodeBrickViewModel>(),
    rootlessStoreWindowSize: RootlessStoreWindowSize,
    onDismissCreationMenu: () -> Unit = {}
){

    val codeBricks by codeBrickViewModel.codeBricks.collectAsState()
    val codeBrickScreenUiState by codeBrickViewModel.codeBrickScreenUiState.collectAsState()

    // Editor show status
    if (codeBrickScreenUiState.isCodeBrickEditorVisible){
        CodeBrickEditor(
            onDismissRequest = {
                codeBrickViewModel.setCodeBrickEditorVisible(false)
            },
            onCancelClick = {
                codeBrickViewModel.setCodeBrickEditorVisible(false)
            },
            onCreateClick = { title, content, context, tileIndex ->
                codeBrickViewModel.addCodeBrick(
                    codeBrickTitle = title,
                    codeBrickContent = content,
                    codeBrickContext = context,
                    boundTileIndex = tileIndex
                )
                codeBrickViewModel.setCodeBrickEditorVisible(false)
            },
        )
    } else if (codeBrickScreenUiState.isExecutionResultVisible) {
        CodeBrickExecutionResultDialog(
            outputLines = codeBrickScreenUiState.executionOutputLines,
            onDismissRequest = codeBrickViewModel::setExecutionResultVisible,
            onDismissClick = codeBrickViewModel::setExecutionResultVisible
        )
    } else if (codeBrickScreenUiState.isCodeBrickSettingsVisible) {
        CodeBrickSetting(
            codeBrickConfig = codeBrickScreenUiState.selectedCodeBrick,
            onDismissRequest = {
                codeBrickViewModel.hideCodeBrickSettings()
            },
            onCancelClick = {
                codeBrickViewModel.hideCodeBrickSettings()
            },
            onUpdateClick = { title, content, context, tileIndex, oldCodeBrickConfig ->
                codeBrickViewModel.updateCodeBrick(
                    codeBrickTitle = title,
                    codeBrickContent = content,
                    codeBrickContext = context,
                    boundTileIndex = tileIndex,
                    oldCodeBrickConfig = oldCodeBrickConfig
                )
                codeBrickViewModel.hideCodeBrickSettings()
            },
            onInstallPluginClick = codeBrickViewModel::installPluginFromCodeBrick
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
            ){ onDismissCreationMenu() }
    ) {
        itemsIndexed(
            items = codeBricks
        ){ index ,codeBrickConfig ->
            CodeBrickPreviewer(
                codeBrickConfig = codeBrickConfig,
                onExecuteClick = codeBrickViewModel::executeCodeBrick,
                onSettingsClick = {
                    codeBrickViewModel.showCodeBrickSettings(codeBrickConfig)
                },
                onDeleteClick = codeBrickViewModel::deleteCodeBrick
            )
        }
    }
}
