package com.baidaidai.rootless_store.data.shell.repository

import com.baidaidai.rootless_store.data.shell.gateway.ExecuteShellGatewayImpl
import com.baidaidai.rootless_store.domain.shell.model.ShellCommandContainer
import com.baidaidai.rootless_store.domain.shell.model.ShellEnvironment
import com.baidaidai.rootless_store.domain.shell.model.ShellResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExecuteShellRepositoryImpl @Inject constructor(
    private val executeShellGatewayImpl: ExecuteShellGatewayImpl
) {

    private fun runCommandByAppShell(commandContent: String) = executeShellGatewayImpl.runCommandByAppShell(commandContent)

    private fun runCommandByADBShell(commandContent: String) = executeShellGatewayImpl.runCommandByADBShell(commandContent)

    private fun runCommandByRootShell(commandContent: String) = executeShellGatewayImpl.runCommandByRootShell(commandContent)

    fun runCommand(shellCommandContainer: ShellCommandContainer): Flow<ShellResult>{
        return when(shellCommandContainer.shellEnvironment){
            ShellEnvironment.AppShell -> runCommandByAppShell(shellCommandContainer.commandContent)
            ShellEnvironment.ADBShell -> runCommandByADBShell(shellCommandContainer.commandContent)
            ShellEnvironment.RootShell -> runCommandByRootShell(shellCommandContainer.commandContent)
        }
    }

}