package com.baidaidai.rootless_store.application.shell


import com.baidaidai.rootless_store.data.shell.gateway.ExecuteShellGatewayImpl
import com.baidaidai.rootless_store.domain.shell.model.ShellCommandContainer
import com.baidaidai.rootless_store.domain.shell.model.ShellEnvironment
import com.baidaidai.rootless_store.domain.shell.model.ShellResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RunCommandUseCase @Inject constructor(
    private val executeShellGatewayImpl: ExecuteShellGatewayImpl,
) {
    operator fun invoke(shellCommandContainer: ShellCommandContainer): Flow<ShellResult> {
        return when(shellCommandContainer.shellEnvironment){
            ShellEnvironment.AppShell -> executeShellGatewayImpl.runCommandByAppShell(shellCommandContainer.commandContent)
            ShellEnvironment.AdbShell -> executeShellGatewayImpl.runCommandByAdbShell(shellCommandContainer.commandContent)
            ShellEnvironment.RootShell -> executeShellGatewayImpl.runCommandByRootShell(shellCommandContainer.commandContent)
        }
    }
}