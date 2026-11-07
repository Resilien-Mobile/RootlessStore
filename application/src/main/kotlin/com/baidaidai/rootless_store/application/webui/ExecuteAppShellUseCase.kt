package com.baidaidai.rootless_store.application.webui

import com.baidaidai.rootless_store.domain.shell.gateway.ExecuteShellGateway
import com.baidaidai.rootless_store.domain.shell.model.ShellResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExecuteAppShellUseCase @Inject constructor(
    private val executeShellGateway: ExecuteShellGateway
) {

    operator fun invoke(
        commandContent: String
    ): Flow<ShellResult> {
        return executeShellGateway.executeCommandByAppShell(commandContent)
    }

}