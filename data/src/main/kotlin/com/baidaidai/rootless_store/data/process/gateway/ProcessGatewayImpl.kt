package com.baidaidai.rootless_store.data.process.gateway

import IProcProcessScannerCallback
import IProcProcessScanner
import com.baidaidai.rootless_store.data.process.mapper.ProcessMapper.toProcessInfo
import com.baidaidai.rootless_store.data.process.shizuku.dto.ProcCmdlineDto
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuPermissionGatewayImpl
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import com.baidaidai.rootless_store.domain.process.error.ProcessError
import com.baidaidai.rootless_store.domain.process.gateway.ProcessGateway
import com.baidaidai.rootless_store.domain.process.model.ProcessInfo
import com.baidaidai.rootless_store.domain.shizuku.api.ShizukuApi
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class ProcessGatewayImpl @Inject constructor(
    private val shizukuPermissionGatewayImpl: ShizukuPermissionGatewayImpl,
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl,
    private val shizukuApi: ShizukuApi
) : ProcessGateway {

    private var procProcessScanner: IProcProcessScanner? = null
    private var isProcProcessScannerBindingRequested = false

    init {

        initializeProcProcessScanner()

    }

    override fun observeProcessesInfoList(
    ): Flow<Result<List<ProcessInfo>, ProcessError>> =
        flow {

            runCatching {

                while (
                    procProcessScanner == null &&
                    shizukuPermissionGatewayImpl.canUseShizuku()
                ) {
                    initializeProcProcessScanner()

                    if (procProcessScanner == null) {
                        delay(100L)
                    }
                }

                Ok(Unit)

            }.onFailure { throwable ->

                emit(
                    Err(
                        ProcessError(
                            errorMessage = "Shizuku Error",
                            errorCause = throwable.stackTraceToString(),
                            errorCompanion = "Shizuku failed while preparing the process scanner."
                        )
                    )
                )
                return@flow

            }.onSuccess{

                while (currentCoroutineContext().isActive) {

                    runCatching {

                        val processInfoList = getProcCmdlineDtoList()
                            .map { procCmdlineDto ->

                                procCmdlineDto.toProcessInfo()

                            }

                        Ok(processInfoList)

                    }.onSuccess { processInfoListResult ->

                        emit(processInfoListResult)
                        return@flow

                    }.onFailure { throwable ->

                        emit(
                            Err(
                                ProcessError(
                                    errorMessage = "Shizuku Error",
                                    errorCause = throwable.stackTraceToString(),
                                    errorCompanion = "Shizuku failed while reading the process list."
                                )
                            )
                        )
                        return@flow

                    }

                    delay(1_000L)

                }
            }

        }

    private fun initializeProcProcessScanner() =
        runCatching {

            // 检测 Shizuku 是否在
            // 如果不在，则两个都关掉，并且退出去
            if (!shizukuPermissionGatewayImpl.canUseShizuku()) {
                procProcessScanner = null
                isProcProcessScannerBindingRequested = false
                return@runCatching
            }

            procProcessScanner = shizukuUserServiceGatewayImpl
                .findProcProcessScannerUserService()

            // 说明连上 UserService 了
            // 也退出
            if (procProcessScanner != null) {
                return@runCatching
            }

            // 说明 shizuku 在，并且是第一次连接
            if (!isProcProcessScannerBindingRequested) {
                shizukuUserServiceGatewayImpl.startProcProcessScannerUserService()
                isProcProcessScannerBindingRequested = true
            }

        }.onFailure {
            procProcessScanner = null
            isProcProcessScannerBindingRequested = false
        }

    override suspend fun killProcessByShizuku(
        progressPid: Int
    ): Result<Boolean, ProcessError> =
        withContext(Dispatchers.IO) {

            runCatching {

                if (!shizukuPermissionGatewayImpl.canUseShizuku()) {
                    return@runCatching Err(
                        ProcessError(
                            errorMessage = "Shizuku Error",
                            errorCause = "The Shizuku is unavailable.",
                            errorCompanion = "Start Shizuku before terminating a process."
                        )
                    )
                }

                if (!shizukuApi.hasShizukuPermission()) {
                    return@runCatching Ok(false)
                }

                val shizukuUserService = shizukuUserServiceGatewayImpl
                    .findShizukuUserService()
                    ?: return@runCatching Err(
                        ProcessError(
                            errorMessage = "Shizuku Error",
                            errorCause = "The Shizuku User Service is not connected.",
                            errorCompanion = "Rootless Store could not reach the Shizuku shell service."
                        )
                    )

                Ok(shizukuUserService.kill(progressPid))

            }.getOrElse { throwable ->

                Err(
                    ProcessError(
                        errorMessage = "Shizuku Error",
                        errorCause = throwable.stackTraceToString(),
                        errorCompanion = "Shizuku failed while terminating this process."
                    )
                )

            }

        }

    private suspend fun getProcCmdlineDtoList(): List<ProcCmdlineDto> {

        val procProcessScanner = requireNotNull(procProcessScanner)

        return suspendCancellableCoroutine { continuation ->

            procProcessScanner.scan(
                object : IProcProcessScannerCallback.Stub() {

                    override fun onResult(processList: MutableList<ProcCmdlineDto>?) {

                        continuation.resume(processList.orEmpty())

                    }

                }
            )

        }

    }

}
