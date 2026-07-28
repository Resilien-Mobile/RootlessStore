package com.baidaidai.rootless_store.application.webui

import com.baidaidai.rootless_store.data.shell.gateway.ExecuteShellGatewayImpl
import com.baidaidai.rootless_store.domain.shell.model.ShellResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExecuteAppShellUseCase @Inject constructor(
    private val executeShellGatewayImpl: ExecuteShellGatewayImpl
) {

    operator fun invoke(
        commandContent: String
    ): Flow<ShellResult> {
        return executeShellGatewayImpl.runCommandByAppShell(commandContent)
    }

}