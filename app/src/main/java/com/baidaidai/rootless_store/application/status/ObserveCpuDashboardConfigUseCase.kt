package com.baidaidai.rootless_store.application.status

import com.baidaidai.rootless_store.data.status.gateway.StoreStatusGatewayImpl
import com.baidaidai.rootless_store.domain.status.model.CpuDashboardConfig
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class ObserveCpuDashboardConfigUseCase @Inject constructor(
    private val storeStatusGatewayImpl: StoreStatusGatewayImpl,
) {
    operator fun invoke(): Flow<CpuDashboardConfig> {
        return flow {
            while (currentCoroutineContext().isActive) {
                val cpuDashboardConfig = getCpuDashboardConfigOnce()

                if (cpuDashboardConfig == null) {
                    delay(CPU_DASHBOARD_RETRY_INTERVAL_MILLIS.milliseconds)
                    continue
                }

                emit(cpuDashboardConfig)
            }
        }
    }

    private suspend fun getCpuDashboardConfigOnce(): CpuDashboardConfig? {
        return coroutineScope {

            // Get available CPU core count
            val availableCpuCoreCount =
                storeStatusGatewayImpl.getCpuCoreCount() ?: return@coroutineScope null
            if (availableCpuCoreCount <= 0) {
                return@coroutineScope null
            }
            val cpuCoreIndexList = (0 until availableCpuCoreCount).toList()

            // Start total CPU sampling
            val totalCoreInfoDeferred = async {
                storeStatusGatewayImpl.getCoreInfo(cpuCoreIndex = null)
            }

            // Start each CPU core sampling
            val cpuCoreInfoDeferredList = cpuCoreIndexList.map { cpuCoreIndex ->
                async {
                    storeStatusGatewayImpl.getCoreInfo(cpuCoreIndex = cpuCoreIndex)
                }
            }

            // Start system uptime query
            val systemUptimeDeferred = async {
                storeStatusGatewayImpl.getSystemUptime()
            }

            // Await all CPU sampling results
            val totalCoreInfo = totalCoreInfoDeferred.await() ?: return@coroutineScope null
            val cpuCoreInfoResultList = cpuCoreInfoDeferredList.awaitAll()
            val systemUptime = systemUptimeDeferred.await() ?: return@coroutineScope null

            // Filter unavailable CPU core results
            val availableCpuCoreInfoList = cpuCoreInfoResultList.filterNotNull()
            if (availableCpuCoreInfoList.isEmpty()) {
                return@coroutineScope null
            }
            val resolvedCpuCoreCount = availableCpuCoreInfoList.size

            // Build CPU dashboard config
            return@coroutineScope CpuDashboardConfig(
                coreCount = resolvedCpuCoreCount,
                totalCoreInfo = totalCoreInfo,
                core = availableCpuCoreInfoList,
                uptime = systemUptime
            )
        }
    }

    private companion object {
        const val CPU_DASHBOARD_RETRY_INTERVAL_MILLIS = 1_000L
    }
}
