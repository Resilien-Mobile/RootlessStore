package com.baidaidai.rootless_store.application.shell


import com.baidaidai.rootless_store.data.shell.gateway.ExecuteShellGatewayImpl
import com.baidaidai.rootless_store.domain.shell.model.ShellCommand
import com.baidaidai.rootless_store.domain.shell.model.ShellEnvironment
import com.baidaidai.rootless_store.domain.shell.model.ShellResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExecuteShellCommandUseCase @Inject constructor(
    private val executeShellGatewayImpl: ExecuteShellGatewayImpl,
) {
    operator fun invoke(shellCommand: ShellCommand): Flow<ShellResult> {
        return when(shellCommand.shellEnvironment){
            ShellEnvironment.AppShell -> executeShellGatewayImpl.executeCommandByAppShell(shellCommand.commandContent)
            ShellEnvironment.AdbShell -> executeShellGatewayImpl.executeCommandByAdbShell(shellCommand.commandContent)
            ShellEnvironment.RootShell -> executeShellGatewayImpl.executeCommandByRootShell(shellCommand.commandContent)
        }
    }
}