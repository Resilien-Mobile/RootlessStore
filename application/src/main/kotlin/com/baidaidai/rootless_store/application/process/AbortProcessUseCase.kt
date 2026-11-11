package com.baidaidai.rootless_store.application.process

import com.baidaidai.rootless_store.domain.process.error.ProcessError
import com.baidaidai.rootless_store.domain.process.gateway.ProcessGateway
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import javax.inject.Inject

class AbortProcessUseCase @Inject constructor(
    private val processGateway: ProcessGateway
) {

    suspend operator fun invoke(
        progressPid: Int
    ): Result<Unit, ProcessError> {

        val processKillResult = runCatching {
            processGateway.killProcessByShizuku(progressPid)
        }.getOrElse { throwable ->
            return Err(
                ProcessError(
                    errorMessage = "Shizuku Error",
                    errorCause = throwable.stackTraceToString(),
                    errorCompanion = "Shizuku failed while terminating this process."
                )
            )
        }

        val isProcessKilled = processKillResult
            .getOrElse { shizukuError ->
                return Err(
                    ProcessError(
                        errorMessage = "Shizuku Error",
                        errorCause = shizukuError.errorCause,
                        errorCompanion = shizukuError.errorCompanion
                    )
                )
            }

        return if (isProcessKilled) {
            Ok(Unit)
        }else{
            Err(
                ProcessError(
                    errorMessage = "Permission Denied",
                    errorCompanion = "This operation requires a higher-privilege context."
                )
            )
        }

    }

}
