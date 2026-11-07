package com.baidaidai.rootless_store.application.shell


import com.baidaidai.rootless_store.domain.shell.gateway.ExecuteShellGateway
import com.baidaidai.rootless_store.domain.shell.model.ShellCommand
import com.baidaidai.rootless_store.domain.shell.model.ShellEnvironment
import com.baidaidai.rootless_store.domain.shell.model.ShellResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExecuteShellCommandUseCase @Inject constructor(
    private val executeShellGateway: ExecuteShellGateway,
) {
    operator fun invoke(shellCommand: ShellCommand): Flow<ShellResult> {
        return when(shellCommand.shellEnvironment){
            ShellEnvironment.AppShell -> executeShellGateway.executeCommandByAppShell(shellCommand.commandContent)
            ShellEnvironment.AdbShell -> executeShellGateway.executeCommandByAdbShell(shellCommand.commandContent)
            ShellEnvironment.RootShell -> executeShellGateway.executeCommandByRootShell(shellCommand.commandContent)
        }
    }
}