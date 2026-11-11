package com.baidaidai.rootless_store.domain.process.gateway

import com.baidaidai.rootless_store.domain.process.error.ProcessError
import com.baidaidai.rootless_store.domain.process.model.ProcessInfo
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow

interface ProcessGateway {

    fun observeProcessesInfoList(): Flow<Result<List<ProcessInfo>, ProcessError>>

    suspend fun killProcessByShizuku(
        progressPid: Int
    ): Result<Boolean, ProcessError>

}
