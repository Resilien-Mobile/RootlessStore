package com.baidaidai.rootless_store.application.status

import com.baidaidai.rootless_store.data.status.gateway.StoreStatusGatewayImpl
import com.baidaidai.rootless_store.domain.status.model.NetDashboardConfig
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class GetNetDashboardConfigUseCase @Inject constructor(
    private val storeStatusGatewayImpl: StoreStatusGatewayImpl
) {
    operator fun invoke(): Flow<NetDashboardConfig> {
        return flow {
            while (currentCoroutineContext().isActive) {
                val netDashboardConfig = getNetDashboardConfigOnce()

                if (netDashboardConfig == null) {
                    delay(NET_DASHBOARD_RETRY_INTERVAL_MILLIS.milliseconds)
                    continue
                }

                // Emit network dashboard config
                emit(netDashboardConfig)
            }
        }
    }

    private suspend fun getNetDashboardConfigOnce(): NetDashboardConfig? {
        return coroutineScope {

            // Get optional network availability
            val isCarrierAvailable = storeStatusGatewayImpl.isCarrierAvailable()
            val isVpnAvailable = storeStatusGatewayImpl.isVpnAvailable()

            // Resolve active network interface names
            val carrierNetworkInterfaceName = if (isCarrierAvailable) {
                storeStatusGatewayImpl.getCarrierNetworkInterfaceName()
            } else {
                null
            }
            val vpnNetworkInterfaceName = if (isVpnAvailable) {
                storeStatusGatewayImpl.getVpnNetworkInterfaceName()
            } else {
                null
            }

            // Start network interface sampling
            val wlanPortInfoDeferred = async {
                storeStatusGatewayImpl.getPortInfo()
            }
            val vpnPortInfoDeferred = async {
                if (isVpnAvailable && vpnNetworkInterfaceName != null) {
                    storeStatusGatewayImpl.getPortInfo(
                        networkInterfaceName = vpnNetworkInterfaceName
                    )
                } else {
                    null
                }
            }
            val carrierPortInfoDeferred = async {
                if (isCarrierAvailable && carrierNetworkInterfaceName != null) {
                    storeStatusGatewayImpl.getPortInfo(
                        networkInterfaceName = carrierNetworkInterfaceName
                    )
                } else {
                    null
                }
            }

            // Await all network interface results
            val wlanPortInfo = wlanPortInfoDeferred.await()
            val vpnPortInfo = vpnPortInfoDeferred.await()
            val carrierPortInfo = carrierPortInfoDeferred.await()

            // Build available network interface list
            val availableNetworkInterfaceList = listOfNotNull(
                wlanPortInfo,
                vpnPortInfo,
                carrierPortInfo
            )
            if (availableNetworkInterfaceList.isEmpty()) {
                return@coroutineScope null
            }

            // Select current network rate source
            // Priority: VPN -> WLAN -> Carrier
            val currentNetworkRateSource =
                vpnPortInfo
                    ?: wlanPortInfo
                    ?: carrierPortInfo
                    ?: return@coroutineScope null

            // Build network dashboard config
            return@coroutineScope NetDashboardConfig(
                currentUploadRate = currentNetworkRateSource.currentUploadRate,
                currentDownloadRate = currentNetworkRateSource.currentDownloadRate,
                port = availableNetworkInterfaceList
            )
        }
    }

    private companion object {
        const val NET_DASHBOARD_RETRY_INTERVAL_MILLIS = 1_000L
    }
}
