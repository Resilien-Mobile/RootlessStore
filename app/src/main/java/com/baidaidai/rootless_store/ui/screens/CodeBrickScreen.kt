package com.baidaidai.rootless_store.ui.screens

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.baidaidai.rootless_store.ui.components.codeBrickScreen.CodeBrickEditor
import com.baidaidai.rootless_store.ui.components.codeBrickScreen.CodeBrickPreviewer
import com.baidaidai.rootless_store.ui.components.codeBrickScreen.CodeBrickResult
import com.baidaidai.rootless_store.ui.components.codeBrickScreen.CodeBrickSetting
import com.baidaidai.rootless_store.ui.model.RootlessStoreCodeBrickViewModel

@Composable
fun CodeBrickScreen(
    contentPaddingValues: PaddingValues,
    codeBrickViewModel: RootlessStoreCodeBrickViewModel = hiltViewModel<RootlessStoreCodeBrickViewModel>()
){

    val codeBrickConfigList by codeBrickViewModel.codeBrickConfigList.collectAsState()
    val codeBrickScreenUIState by codeBrickViewModel.codeBrickScreenUIState.collectAsState()

    if (codeBrickScreenUIState.brickEditorCanShow){
        CodeBrickEditor(
            onDismissRequest = {
                codeBrickViewModel.changeEditorShowStatus(false)
            },
            onDismissButtonClick = {
                codeBrickViewModel.changeEditorShowStatus(false)
            },
            onConfirmButtonClick = { title, content, context, tileIndex ->
                codeBrickViewModel.createOneCodeBrick(
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
                codeBrickViewModel.updateOneCodeBrick(
                    codeBrickTitle = title,
                    codeBrickContent = content,
                    codeBrickContext = context,
                    bindTileIndex = tileIndex,
                    oldCodeBrickConfig = oldCodeBrickConfig
                )
                codeBrickViewModel.closeSettingShowStatus(false)
            },
            onCodeBrickToPluginButtonClick = { codeBrickConfig -> codeBrickViewModel.convertOneCodeBrickToPlugin(codeBrickConfig) }
        )
    }

    LazyVerticalStaggeredGrid(
        modifier = Modifier
            .padding(contentPaddingValues)
            .fillMaxSize()
        ,
        verticalItemSpacing = 10.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(10.dp)
    ) {
        itemsIndexed(
            items = codeBrickConfigList
        ){ index ,codeBrickConfig ->
            CodeBrickPreviewer(
                codeBrickConfig = codeBrickConfig,
                onActionButtonClick = codeBrickViewModel::executeOneCodeBrick,
                onSettingButtonClick = {
                    codeBrickViewModel.openSettingShowStatus(
                        codeBrickConfig = codeBrickConfig,
                        settingShowStatus = true
                    )
                },
                onDeleteButtonClick = codeBrickViewModel::deleteOneCodeBrick
            )
        }
    }
}
