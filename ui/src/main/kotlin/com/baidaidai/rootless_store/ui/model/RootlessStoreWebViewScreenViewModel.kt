package com.baidaidai.rootless_store.ui.model

import androidx.lifecycle.ViewModel
import com.baidaidai.rootless_store.application.webui.ExecuteAppShellUseCase
import com.baidaidai.rootless_store.application.webui.KernelSuJavaScriptBridge
import com.baidaidai.rootless_store.domain.shell.model.ShellResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class RootlessStoreWebViewScreenViewModel @Inject constructor(
    private val executeAppShellUseCase: ExecuteAppShellUseCase,
    private val kernelSuJavaScriptBridge: KernelSuJavaScriptBridge
): ViewModel() {

    fun getKernelSuJavaScriptBridge(): KernelSuJavaScriptBridge {
        return kernelSuJavaScriptBridge
    }

    fun executeAppShell(
        commandContent: String
    ): Flow<ShellResult> {
        return executeAppShellUseCase(commandContent)
    }

}
