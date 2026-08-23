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
    val codeBrickScreenUIState by codeBrickViewModel.codeBrickScreenUIState.collectAsState()

    // Editor show status
    if (codeBrickScreenUIState.brickEditorCanShow){
        CodeBrickEditor(
            onDismissRequest = {
                codeBrickViewModel.changeEditorShowStatus(false)
            },
            onDismissButtonClick = {
                codeBrickViewModel.changeEditorShowStatus(false)
            },
            onConfirmButtonClick = { title, content, context, tileIndex ->
                codeBrickViewModel.createCodeBrick(
                    codeBrickTitle = title,
                    codeBrickContent = content,
                    codeBrickContext = context,
                    bindTileIndex = tileIndex
                )
                codeBrickViewModel.changeEditorShowStatus(false)
            },
        )
    } else if (codeBrickScreenUIState.executeResultCanShow) {
        CodeBrickResult(
            resultContent = codeBrickScreenUIState.executeResultContent,
            onDismissRequest = codeBrickViewModel::changeResultShowStatus,
            onConfirmButtonClick = codeBrickViewModel::changeResultShowStatus
        )
    } else if (codeBrickScreenUIState.brickSettingCanShow) {
        CodeBrickSetting(
            codeBrickConfig = codeBrickScreenUIState.handlingCodeBrickConfig,
            onDismissRequest = {
                codeBrickViewModel.closeSettingShowStatus(false)
            },
            onDismissButtonClick = {
                codeBrickViewModel.closeSettingShowStatus(false)
            },
            onConfirmButtonClick = { title, content, context, tileIndex, oldCodeBrickConfig ->
                codeBrickViewModel.updateCodeBrick(
                    codeBrickTitle = title,
                    codeBrickContent = content,
                    codeBrickContext = context,
                    bindTileIndex = tileIndex,
                    oldCodeBrickConfig = oldCodeBrickConfig
                )
                codeBrickViewModel.closeSettingShowStatus(false)
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
                    codeBrickViewModel.openSettingShowStatus(
                        codeBrickConfig = codeBrickConfig,
                        settingShowStatus = true
                    )
                },
                onDeleteButtonClick = codeBrickViewModel::deleteCodeBrick
            )
        }
    }
}
