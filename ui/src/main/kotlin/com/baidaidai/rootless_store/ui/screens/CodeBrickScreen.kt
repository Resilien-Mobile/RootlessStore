package com.baidaidai.rootless_store.ui.screens

import com.baidaidai.rootless_store.ui.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.baidaidai.rootless_store.ui.components.codeBrickScreen.CodeBrickEditor
import com.baidaidai.rootless_store.ui.components.codeBrickScreen.CodeBrickPreviewer
import com.baidaidai.rootless_store.ui.components.codeBrickScreen.CodeBrickExecutionResultDialog
import com.baidaidai.rootless_store.ui.components.codeBrickScreen.CodeBrickSetting
import com.baidaidai.rootless_store.ui.model.RootlessStoreCodeBrickViewModel

@Composable
fun CodeBrickScreen(
    contentPaddingValues: PaddingValues,
    codeBrickViewModel: RootlessStoreCodeBrickViewModel = hiltViewModel<RootlessStoreCodeBrickViewModel>(),
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

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPaddingValues)
    ) {

        val resolveCodeBrickGridCount = remember(maxWidth){
            resolveCodeBrickGridCount(
                containerWidth = maxWidth,
                contentPaddingValues = 10.dp
            )
        }

        if(codeBricks.isEmpty()){

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ){ onDismissCreationMenu() }
            ) {
                Icon(
                    painter = painterResource(R.drawable.material_symbols_brick),
                    modifier = Modifier.size(64.dp),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
                )
                Text(
                    text = "No Code Brick Added",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
                )
            }

        }else{

            LazyVerticalStaggeredGrid(
                verticalItemSpacing = 10.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                columns = StaggeredGridCells.Fixed(resolveCodeBrickGridCount),
                contentPadding = PaddingValues(10.dp),
                modifier = Modifier
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


    }
}

/**
 * 按 200.dp 来算，可以吃完后剩下多少可用空间
 * 如果 剩余空间 >= 158dp ，则应该增加一列，让所有 Preview 自己缩减来适配
 * 如果 剩余空间 < 158dp ，则不用变化剩下的宽度
 */
private fun resolveCodeBrickGridCount(
    containerWidth: Dp,
    contentPaddingValues: Dp,
    minItemWidth: Dp = 150.dp,
    maxItemWidth: Dp = 200.dp,
    horizontalSpacing: Dp = 8.dp,
    minColumns: Int = 2,
    maxColumns: Int = 100
): Int {
    val contentWidth = containerWidth - contentPaddingValues * 2

    // 先算，按200dp来能排多少列
    val columnsAtMaxWidth = ((contentWidth + horizontalSpacing) / (maxItemWidth + horizontalSpacing))
        .toInt()
        .coerceAtLeast(1)

    // 算算用掉的 Width 空间
    val usedWidthAtMax =
        maxItemWidth * columnsAtMaxWidth +
                horizontalSpacing * (columnsAtMaxWidth - 1)

    val remainingWidth = contentWidth - usedWidthAtMax

    val canFitOneMoreColumn = remainingWidth >= minItemWidth + horizontalSpacing

    val resolvedColumns = if (canFitOneMoreColumn) {
        columnsAtMaxWidth + 1
    } else {
        columnsAtMaxWidth
    }

    return resolvedColumns.coerceIn(minColumns, maxColumns)
}
