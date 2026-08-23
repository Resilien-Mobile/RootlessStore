package com.baidaidai.rootless_store.ui.model

import android.content.Context
import androidx.lifecycle.ViewModel
import com.baidaidai.rootless_store.application.webui.ExecuteAppShellUseCase
import com.baidaidai.rootless_store.application.webui.KernelSuCompatible
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import com.baidaidai.rootless_store.domain.shell.model.ShellResult
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class RootlessStoreWebViewScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl,
    private val executeAppShellUseCase: ExecuteAppShellUseCase
): ViewModel() {

    fun createKernelSuCompatible(): KernelSuCompatible {
        return KernelSuCompatible(
            context = context,
            shizukuUserServiceGatewayImpl = shizukuUserServiceGatewayImpl
        )
    }

    fun executeAppShellUseCase(
        commandContent: String
    ): Flow<ShellResult> {
        return executeAppShellUseCase.invoke(commandContent)
    }

}