package com.baidaidai.rootless_store.application.process

import com.baidaidai.rootless_store.domain.process.error.ProcessError
import com.baidaidai.rootless_store.domain.process.gateway.ProcessGateway
import com.baidaidai.rootless_store.domain.process.model.ProcessInfo
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveProcessesUseCase @Inject constructor(
    private val processGateway: ProcessGateway
) {

    operator fun invoke(): Flow<Result<List<ProcessInfo>, ProcessError>> {

        return processGateway.observeProcessesInfoList()

    }

}
