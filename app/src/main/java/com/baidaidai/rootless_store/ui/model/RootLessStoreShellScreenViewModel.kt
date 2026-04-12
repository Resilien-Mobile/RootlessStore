package com.baidaidai.rootless_store.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.data.shell.repository.ExecuteShellRepositoryImpl
import com.baidaidai.rootless_store.domain.shell.model.ShellCommandContainer
import com.baidaidai.rootless_store.domain.shell.model.ShellResult
import com.baidaidai.rootless_store.domain.shell.usecase.RunCommandUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootLessStoreShellScreenViewModel @Inject constructor(
    private val executeShellRepositoryImpl: ExecuteShellRepositoryImpl,
    private val runCommandUseCase: RunCommandUseCase
) : ViewModel(){

    private var _shellOutputList = MutableStateFlow(emptyList<ShellResult>())
    val shellOutputList = _shellOutputList.asStateFlow()

    fun runCommand(shellCommandContainer: ShellCommandContainer){
        viewModelScope.launch {
            val shellOutput = runCommandUseCase(shellCommandContainer)
            shellOutput.collect { shellResult ->
                _shellOutputList.value += shellResult
            }
        }
    }

}