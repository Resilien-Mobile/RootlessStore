package com.baidaidai.rootless_store.domain.shell.gateway

import com.baidaidai.rootless_store.domain.shell.model.ShellResult
import kotlinx.coroutines.flow.Flow

interface ExecuteShellGateway {

    fun executeCommandByAppShell(
        commandContent: String
    ): Flow<ShellResult>

    fun executeCommandByAdbShell(
        commandContent: String
    ): Flow<ShellResult>

    fun executeCommandByRootShell(
        commandContent: String
    ): Flow<ShellResult>

}
