package com.baidaidai.rootless_store.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.domain.process.model.ProcessInfo
import com.baidaidai.rootless_store.ui.R
import com.baidaidai.rootless_store.ui.components.common.VerticalSpacer
import com.baidaidai.rootless_store.ui.components.processManager.ProcessManagerListItem
import com.baidaidai.rootless_store.ui.model.ProcessManagerUiState
import com.baidaidai.rootless_store.ui.theme.getExpressiveListItemShape
import kotlinx.coroutines.launch

@Composable
fun ProcessManagerScreen(
    processList: List<ProcessInfo>,
    uiState: ProcessManagerUiState,
    contentPadding: PaddingValues,
    onKillProcess: suspend (Int) -> Boolean,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    if (uiState.isProcessListUnavailable) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxSize()
                .padding(top = contentPadding.calculateTopPadding())
        ) {
            Icon(
                painter = painterResource(R.drawable.material_symbols_disabled),
                modifier = Modifier.size(64.dp),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Text(
                text = stringResource(R.string.process_manager_empty_state_message),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }

    } else {

        LazyColumn(
            contentPadding = PaddingValues(
                bottom = contentPadding.calculateBottomPadding(),
            ),
            modifier = modifier
                .fillMaxSize()
                .padding(
                    top = contentPadding.calculateTopPadding()
                )
                .padding(horizontal = 16.dp)
        ) {

            itemsIndexed(
                items = processList,
                key = { _, processInfo -> processInfo.pid },
            ) { index, processInfo ->
                val swipeToDismissBoxState = rememberSwipeToDismissBoxState()

                SwipeToDismissBox(
                    state = swipeToDismissBoxState,
                    enableDismissFromStartToEnd = false,
                    enableDismissFromEndToStart = true,
                    onDismiss = { dismissDirection ->
                        if (dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                            coroutineScope.launch {
                                val isProcessKilled = onKillProcess(processInfo.pid)
                                if (!isProcessKilled) {
                                    swipeToDismissBoxState.reset()
                                }
                            }
                        }
                    },
                    backgroundContent = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.errorContainer),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.material_symbols_delete),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(end = 16.dp)
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(getExpressiveListItemShape(index, processList))
                ) {
                    ProcessManagerListItem(
                        processInfo = processInfo
                    )
                }

                if (index != processList.lastIndex) {
                    VerticalSpacer(2.dp)
                }

            }

        }

    }

}
