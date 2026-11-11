package com.baidaidai.rootless_store.ui.layout.processManager

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.baidaidai.rootless_store.ui.components.processManager.ProcessManagerErrorDialog
import com.baidaidai.rootless_store.ui.model.ProcessManagerViewModel
import com.baidaidai.rootless_store.ui.screens.ProcessManagerScreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProcessManagerActivityLayout(
    processManagerViewModel: ProcessManagerViewModel = hiltViewModel()
) {

    val processListFlow = remember(processManagerViewModel) {
        processManagerViewModel.observeProcessesInfoList()
    }
    val processList by processListFlow.collectAsState(initial = emptyList())
    val processManagerUiState by processManagerViewModel.uiState.collectAsState()
    val processError by processManagerViewModel.processError.collectAsState()

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text("Process")
                },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
            )
        }
    ) { contentPadding ->

        ProcessManagerScreen(
            processList = processList,
            uiState = processManagerUiState,
            contentPadding = contentPadding,
            onKillProcess = processManagerViewModel::killProcess
        )

    }

    processError?.let { error ->
        ProcessManagerErrorDialog(
            processError = error,
            onDismiss = processManagerViewModel::clearProcessError
        )
    }

}
