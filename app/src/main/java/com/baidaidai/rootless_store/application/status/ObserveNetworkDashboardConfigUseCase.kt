package com.baidaidai.rootless_store.application.status

import com.baidaidai.rootless_store.data.status.gateway.StoreStatusGatewayImpl
import com.baidaidai.rootless_store.domain.status.model.NetworkDashboardConfig
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class ObserveNetworkDashboardConfigUseCase @Inject constructor(
    private val storeStatusGatewayImpl: StoreStatusGatewayImpl
) {
    operator fun invoke(): Flow<NetworkDashboardConfig> {
        return flow {
            while (currentCoroutineContext().isActive) {
                val networkDashboardConfig = getNetworkDashboardConfigOnce()

                if (networkDashboardConfig == null) {
                    delay(NETWORK_DASHBOARD_RETRY_INTERVAL_MILLIS.milliseconds)
                    continue
                }

                // Emit network dashboard config
                emit(networkDashboardConfig)
            }
        }
    }

    private suspend fun getNetworkDashboardConfigOnce(): NetworkDashboardConfig? {
        return coroutineScope {

            // Get optional network availability
            val isCarrierAvailable = storeStatusGatewayImpl.isCarrierAvailable()
            val isVpnAvailable = storeStatusGatewayImpl.isVpnAvailable()

            // Resolve active network interface names
            val carrierNetworkInterfaceName = if (isCarrierAvailable) {
                storeStatusGatewayImpl.findCarrierNetworkInterfaceName()
            } else {
                null
            }
            val vpnNetworkInterfaceName = if (isVpnAvailable) {
                storeStatusGatewayImpl.findVpnNetworkInterfaceName()
            } else {
                null
            }

            // Start network interface sampling
            val wlanNetworkInterfaceMetricsDeferred = async {
                storeStatusGatewayImpl.findNetworkInterfaceMetrics()
            }
            val vpnNetworkInterfaceMetricsDeferred = async {
                if (isVpnAvailable && vpnNetworkInterfaceName != null) {
                    storeStatusGatewayImpl.findNetworkInterfaceMetrics(
                        networkInterfaceName = vpnNetworkInterfaceName
                    )
                } else {
                    null
                }
            }
            val carrierNetworkInterfaceMetricsDeferred = async {
                if (isCarrierAvailable && carrierNetworkInterfaceName != null) {
                    storeStatusGatewayImpl.findNetworkInterfaceMetrics(
                        networkInterfaceName = carrierNetworkInterfaceName
                    )
                } else {
                    null
                }
            }

            // Await all network interface results
            val wlanNetworkInterfaceMetrics = wlanNetworkInterfaceMetricsDeferred.await()
            val vpnNetworkInterfaceMetrics = vpnNetworkInterfaceMetricsDeferred.await()
            val carrierNetworkInterfaceMetrics = carrierNetworkInterfaceMetricsDeferred.await()

            // Build available network interface list
            val availableNetworkInterfaces = listOfNotNull(
                wlanNetworkInterfaceMetrics,
                vpnNetworkInterfaceMetrics,
                carrierNetworkInterfaceMetrics
            )
            if (availableNetworkInterfaces.isEmpty()) {
                return@coroutineScope null
            }

            // Select current network rate source
            // Priority: VPN -> WLAN -> Carrier
            val currentNetworkRateSource =
                vpnNetworkInterfaceMetrics
                    ?: wlanNetworkInterfaceMetrics
                    ?: carrierNetworkInterfaceMetrics
                    ?: return@coroutineScope null

            // Build network dashboard config
            return@coroutineScope NetworkDashboardConfig(
                currentUploadRate = currentNetworkRateSource.currentUploadRate,
                currentDownloadRate = currentNetworkRateSource.currentDownloadRate,
                networkInterfaces = availableNetworkInterfaces
            )
        }
    }

    private companion object {
        const val NETWORK_DASHBOARD_RETRY_INTERVAL_MILLIS = 1_000L
    }
}
