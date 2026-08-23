package com.baidaidai.rootless_store.data.status.datasource

import IShellService
import android.os.RemoteException
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import com.baidaidai.rootless_store.data.shizuku.server.ShizukuEndpointCallback
import com.baidaidai.rootless_store.domain.status.model.CpuCoreMetrics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class CpuStatusDataSource @Inject constructor(
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl
) {

    suspend operator fun invoke(cpuCoreIndex: Int? = null): CpuCoreMetrics? {

        // Get Shizuku user service
        val shizukuUserService =
            shizukuUserServiceGatewayImpl.findShizukuUserService() ?: return null

        // Read the first CPU snapshot
        val firstCpuSnapshot = readCpuSnapshot(
            shizukuUserService = shizukuUserService,
            cpuCoreIndex = cpuCoreIndex
        ) ?: return null

        // Wait before reading the second CPU snapshot
        delay(CPU_SAMPLE_INTERVAL_MILLIS.milliseconds)

        // Read the second CPU snapshot
        val secondCpuSnapshot = readCpuSnapshot(
            shizukuUserService = shizukuUserService,
            cpuCoreIndex = cpuCoreIndex
        ) ?: return null

        // Convert the two snapshots into CPU delta information
        return calculateCpuCoreMetrics(
            firstCpuSnapshot = firstCpuSnapshot,
            secondCpuSnapshot = secondCpuSnapshot
        )
    }

    suspend fun findCpuCoreCount(): Int? {

        // Get Shizuku user service
        val shizukuUserService =
            shizukuUserServiceGatewayImpl.findShizukuUserService() ?: return null

        // Read CPU core count
        val cpuCoreCountCommandResult = executeSingleLineCommand(
            shizukuUserService = shizukuUserService,
            commandContent = CPU_CORE_COUNT_COMMAND
        ) ?: return null

        // Parse CPU core count
        val cpuCoreCount = cpuCoreCountCommandResult
            .trim()
            .toIntOrNull() ?: return null

        return cpuCoreCount
    }

    suspend fun findSystemUptime(): Duration? {

        // Get Shizuku user service
        val shizukuUserService =
            shizukuUserServiceGatewayImpl.findShizukuUserService() ?: return null

        // Read system uptime
        val systemUptimeCommandResult = executeSingleLineCommand(
            shizukuUserService = shizukuUserService,
            commandContent = SYSTEM_UPTIME_COMMAND
        ) ?: return null

        // Parse the first value as system uptime seconds
        val systemUptimeSecondsContent = systemUptimeCommandResult
            .trim()
            .substringBefore(" ")
        val systemUptimeSeconds = systemUptimeSecondsContent.toDoubleOrNull() ?: return null
        val systemUptime = systemUptimeSeconds.seconds

        return systemUptime
    }

    private suspend fun readCpuSnapshot(
        shizukuUserService: IShellService,
        cpuCoreIndex: Int?
    ): CpuSnapshot? {
        val cpuCoreName = cpuCoreIndex?.let { index ->
            "cpu$index"
        } ?: TOTAL_CPU_CORE_NAME
        val cpuReadCommand = """
            awk -v target="$cpuCoreName" '${'$'}1 == target { print; found = 1; exit } END { if (!found) print "$CPU_NOT_FOUND_MARKER" }' /proc/stat
        """.trimIndent()

        val cpuStatLine = executeSingleLineCommand(
            shizukuUserService = shizukuUserService,
            commandContent = cpuReadCommand
        ) ?: return null

        if (cpuStatLine == CPU_NOT_FOUND_MARKER) {
            return null
        }

        return parseCpuSnapshot(cpuStatLine)
    }

    private suspend fun executeSingleLineCommand(
        shizukuUserService: IShellService,
        commandContent: String
    ): String? {
        val commandResult = withTimeoutOrNull(CPU_READ_TIMEOUT_MILLIS.milliseconds) {
            callbackFlow {
                val callback = ShizukuEndpointCallback(
                    onExecuteCallback = { session ->
                        trySend(session)
                    },
                    onErrorCallback = {
                        trySend(null)
                    },
                    onProcessExitedCallback = {}
                )

                launch(Dispatchers.IO) {
                    try {
                        shizukuUserService.command(
                            commandContent,
                            callback,
                            false
                        )
                    } catch (_: RemoteException) {
                        trySend(null)
                    }
                }

                awaitClose {}
            }.first()
        } ?: return null

        return commandResult
    }

    private fun parseCpuSnapshot(cpuStatLine: String): CpuSnapshot? {
        val cpuStatFields = cpuStatLine
            .trim()
            .split(Regex("\\s+"))

        if (cpuStatFields.size < CPU_REQUIRED_FIELD_COUNT) {
            return null
        }

        return try {
            CpuSnapshot(
                user = cpuStatFields[1].toLong(),
                nice = cpuStatFields[2].toLong(),
                system = cpuStatFields[3].toLong(),
                idle = cpuStatFields[4].toLong(),
                ioWait = cpuStatFields[5].toLong(),
                irq = cpuStatFields[6].toLong(),
                softIrq = cpuStatFields[7].toLong(),
                steal = cpuStatFields[8].toLong()
            )
        } catch (_: NumberFormatException) {
            null
        }
    }

    private fun calculateCpuCoreMetrics(
        firstCpuSnapshot: CpuSnapshot,
        secondCpuSnapshot: CpuSnapshot
    ): CpuCoreMetrics {
        val userDelta = calculateDelta(
            firstValue = firstCpuSnapshot.user + firstCpuSnapshot.nice,
            secondValue = secondCpuSnapshot.user + secondCpuSnapshot.nice
        )
        val systemDelta = calculateDelta(
            firstValue = firstCpuSnapshot.system + firstCpuSnapshot.irq + firstCpuSnapshot.softIrq,
            secondValue = secondCpuSnapshot.system + secondCpuSnapshot.irq + secondCpuSnapshot.softIrq
        )
        val idleDelta = calculateDelta(
            firstValue = firstCpuSnapshot.idle,
            secondValue = secondCpuSnapshot.idle
        )
        val ioWaitDelta = calculateDelta(
            firstValue = firstCpuSnapshot.ioWait,
            secondValue = secondCpuSnapshot.ioWait
        )
        val stealDelta = calculateDelta(
            firstValue = firstCpuSnapshot.steal,
            secondValue = secondCpuSnapshot.steal
        )
        val totalDelta = userDelta + systemDelta + idleDelta + ioWaitDelta + stealDelta
        val totalPercent = if (totalDelta == 0L) {
            0f
        } else {
            (totalDelta - idleDelta).toFloat() / totalDelta.toFloat()
        }

        return CpuCoreMetrics(
            userDelta = userDelta.toInt(),
            systemDelta = systemDelta.toInt(),
            idleDelta = idleDelta.toInt(),
            ioWaitDelta = ioWaitDelta.toInt(),
            stealDelta = stealDelta.toInt(),
            totalPercent = totalPercent
        )
    }

    private fun calculateDelta(
        firstValue: Long,
        secondValue: Long
    ): Long {
        return (secondValue - firstValue).coerceAtLeast(0L)
    }

    private data class CpuSnapshot(
        val user: Long,
        val nice: Long,
        val system: Long,
        val idle: Long,
        val ioWait: Long,
        val irq: Long,
        val softIrq: Long,
        val steal: Long
    )

    private companion object {
        const val CPU_SAMPLE_INTERVAL_MILLIS = 1_000L
        const val CPU_READ_TIMEOUT_MILLIS = 3_000L
        const val CPU_REQUIRED_FIELD_COUNT = 9
        const val CPU_NOT_FOUND_MARKER = "__CPU_NOT_FOUND__"
        const val TOTAL_CPU_CORE_NAME = "cpu"
        const val CPU_CORE_COUNT_COMMAND = "nproc --all"
        const val SYSTEM_UPTIME_COMMAND = "cat /proc/uptime"
    }
}
