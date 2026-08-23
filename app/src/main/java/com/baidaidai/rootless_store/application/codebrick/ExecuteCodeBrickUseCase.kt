package com.baidaidai.rootless_store.application.codebrick

import android.util.Log
import com.baidaidai.rootless_store.data.shell.gateway.ExecuteShellGatewayImpl
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import com.baidaidai.rootless_store.domain.shell.model.ShellResult
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExecuteCodeBrickUseCase @Inject constructor(
    private val executeShellGatewayImpl: ExecuteShellGatewayImpl,
) {
    operator fun invoke(codeBrickConfig: CodeBrickConfig): Flow<ShellResult> {
        Log.d("ExecuteCodeBrickUseCase, codeBrickContent",codeBrickConfig.codeBrickContent)
        return when(codeBrickConfig.codeBrickEnvironment){
            HosterOverallStatus.LIMITED,
            HosterOverallStatus.PERMISSIVE -> executeShellGatewayImpl.runCommandByAppShell(codeBrickConfig.codeBrickContent)

            HosterOverallStatus.ADB -> executeShellGatewayImpl.runCommandByADBShell(codeBrickConfig.codeBrickContent)
            HosterOverallStatus.ROOTD -> executeShellGatewayImpl.runCommandByRootShell(codeBrickConfig.codeBrickContent)
        }
    }
}
