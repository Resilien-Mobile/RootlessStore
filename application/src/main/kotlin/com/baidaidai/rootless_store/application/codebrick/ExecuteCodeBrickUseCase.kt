package com.baidaidai.rootless_store.application.codebrick

import android.util.Log
import com.baidaidai.rootless_store.domain.shell.gateway.ExecuteShellGateway
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import com.baidaidai.rootless_store.domain.shell.model.ShellResult
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExecuteCodeBrickUseCase @Inject constructor(
    private val executeShellGateway: ExecuteShellGateway,
) {
    operator fun invoke(codeBrickConfig: CodeBrickConfig): Flow<ShellResult> {
        Log.d("ExecuteCodeBrickUseCase, codeBrickContent",codeBrickConfig.codeBrickContent)
        return when(codeBrickConfig.codeBrickEnvironment){
            ExecutionContext.LIMITED,
            ExecutionContext.PERMISSIVE -> executeShellGateway.executeCommandByAppShell(codeBrickConfig.codeBrickContent)

            ExecutionContext.ADB -> executeShellGateway.executeCommandByAdbShell(codeBrickConfig.codeBrickContent)
            ExecutionContext.ROOTD -> executeShellGateway.executeCommandByRootShell(codeBrickConfig.codeBrickContent)
        }
    }
}
