package com.baidaidai.rootless_store.application.codebrick

import android.util.Log
import com.baidaidai.rootless_store.data.shell.gateway.ExecuteShellGatewayImpl
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import com.baidaidai.rootless_store.domain.shell.model.ShellResult
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExecuteCodeBrickUseCase @Inject constructor(
    private val executeShellGatewayImpl: ExecuteShellGatewayImpl,
) {
    operator fun invoke(codeBrickConfig: CodeBrickConfig): Flow<ShellResult> {
        Log.d("ExecuteCodeBrickUseCase, codeBrickContent",codeBrickConfig.codeBrickContent)
        return when(codeBrickConfig.codeBrickEnvironment){
            ExecutionContext.LIMITED,
            ExecutionContext.PERMISSIVE -> executeShellGatewayImpl.executeCommandByAppShell(codeBrickConfig.codeBrickContent)

            ExecutionContext.ADB -> executeShellGatewayImpl.executeCommandByAdbShell(codeBrickConfig.codeBrickContent)
            ExecutionContext.ROOTD -> executeShellGatewayImpl.executeCommandByRootShell(codeBrickConfig.codeBrickContent)
        }
    }
}
