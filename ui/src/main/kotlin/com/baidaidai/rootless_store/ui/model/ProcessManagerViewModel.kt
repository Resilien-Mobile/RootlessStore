package com.baidaidai.rootless_store.ui.model

import androidx.lifecycle.ViewModel
import com.baidaidai.rootless_store.application.process.AbortProcessUseCase
import com.baidaidai.rootless_store.application.process.ObserveProcessesUseCase
import com.baidaidai.rootless_store.domain.process.error.ProcessError
import com.baidaidai.rootless_store.domain.process.model.ProcessInfo
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.onErr
import com.github.michaelbull.result.onOk
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class ProcessManagerUiState(
    val isProcessListUnavailable: Boolean = true
)

@HiltViewModel
class ProcessManagerViewModel @Inject constructor(
    private val observeProcessesUseCase: ObserveProcessesUseCase,
    private val abortProcessUseCase: AbortProcessUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProcessManagerUiState())
    val uiState: StateFlow<ProcessManagerUiState> = _uiState.asStateFlow()

    private val _processError = MutableStateFlow<ProcessError?>(null)
    val processError = _processError.asStateFlow()

    fun observeProcessesInfoList(): Flow<List<ProcessInfo>> =
        observeProcessesUseCase()
            .map { processInfoListResult ->

                processInfoListResult
                    .onOk { processInfoList ->
                        _uiState.value = ProcessManagerUiState(
                            isProcessListUnavailable = processInfoList.isEmpty()
                        )
                    }
                    .onErr { processError ->
                        _uiState.value = ProcessManagerUiState(
                            isProcessListUnavailable = true
                        )
                        _processError.value = processError
                    }
                    .getOrElse { emptyList() }

            }

    suspend fun killProcess(
        progressPid: Int
    ): Boolean {

        _processError.value = null

        abortProcessUseCase(progressPid)
            .getOrElse { processError ->
                _processError.value = processError
                return false
            }

        return true

    }

    fun clearProcessError() {

        _processError.value = null

    }

}
