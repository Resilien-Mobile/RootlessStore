package com.baidaidai.rootless_store.data.status.datasource

import IShellService
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.RemoteException
import com.baidaidai.rootless_store.data.R
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import com.baidaidai.rootless_store.data.shizuku.server.ShizukuEndpointCallback
import com.baidaidai.rootless_store.domain.status.model.NetworkInterfaceMetrics
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class NetworkStatusDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    suspend operator fun invoke(
        networkInterfaceName: String = DEFAULT_NETWORK_INTERFACE_NAME
    ): NetworkInterfaceMetrics? {

        // Get Shizuku user service
        val shizukuUserService =
            shizukuUserServiceGatewayImpl.findShizukuUserService() ?: return null

        // Read the first network interface snapshot
        val firstNetworkSnapshot = readNetworkSnapshot(
            shizukuUserService = shizukuUserService,
            networkInterfaceName = networkInterfaceName
        ) ?: return null

        // Wait before reading the second network interface snapshot
        delay(NETWORK_SAMPLE_INTERVAL_MILLIS.milliseconds)

        // Read the second network interface snapshot
        val secondNetworkSnapshot = readNetworkSnapshot(
            shizukuUserService = shizukuUserService,
            networkInterfaceName = networkInterfaceName
        ) ?: return null

        // Read the network interface IPv4 address
        val networkInterfaceAddress = readNetworkInterfaceAddress(
            shizukuUserService = shizukuUserService,
            networkInterfaceName = networkInterfaceName
        ) ?: return null

        // Convert the two snapshots into network interface information
        return calculateNetworkInterfaceMetrics(
            networkInterfaceName = networkInterfaceName,
            networkInterfaceAddress = networkInterfaceAddress,
            firstNetworkSnapshot = firstNetworkSnapshot,
            secondNetworkSnapshot = secondNetworkSnapshot
        )
    }

    fun isCarrierAvailable(): Boolean {
        return findNetworkByTransport(NetworkCapabilities.TRANSPORT_CELLULAR) != null
    }

    fun isVpnAvailable(): Boolean {
        return findNetworkByTransport(NetworkCapabilities.TRANSPORT_VPN) != null
    }

    fun findCarrierNetworkInterfaceName(): String? {
        return findNetworkInterfaceName(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    fun findVpnNetworkInterfaceName(): String? {
        return findNetworkInterfaceName(NetworkCapabilities.TRANSPORT_VPN)
    }

    private suspend fun readNetworkSnapshot(
        shizukuUserService: IShellService,
        networkInterfaceName: String
    ): NetworkSnapshot? {
        val networkReadCommand = """
            awk -v target="$networkInterfaceName:" '${'$'}1 == target { print ${'$'}2, ${'$'}10; found = 1; exit } END { if (!found) print "$NETWORK_INTERFACE_NOT_FOUND_MARKER" }' /proc/net/dev
        """.trimIndent()

        val networkStatLine = executeSingleLineCommand(
            shizukuUserService = shizukuUserService,
            commandContent = networkReadCommand
        ) ?: return null

        if (networkStatLine == NETWORK_INTERFACE_NOT_FOUND_MARKER) {
            return null
        }

        return parseNetworkSnapshot(networkStatLine)
    }

    private suspend fun readNetworkInterfaceAddress(
        shizukuUserService: IShellService,
        networkInterfaceName: String
    ): String? {
        val networkAddressCommand = """
            ip -o -4 addr show dev "$networkInterfaceName" | awk 'NR == 1 { split(${'$'}4, address, "/"); print address[1] }'
        """.trimIndent()

        val networkInterfaceAddress = executeSingleLineCommand(
            shizukuUserService = shizukuUserService,
            commandContent = networkAddressCommand
        )
            ?.trim()
            ?.takeIf { address -> address.isNotEmpty() }

        return networkInterfaceAddress
    }

    private suspend fun executeSingleLineCommand(
        shizukuUserService: IShellService,
        commandContent: String
    ): String? {
        val commandResult = withTimeoutOrNull(NETWORK_READ_TIMEOUT_MILLIS.milliseconds) {
            callbackFlow {
                val callback = ShizukuEndpointCallback(
                    onOutput = { output ->
                        trySend(output)
                    },
                    onErrors = {
                        trySend(null)
                    },
                    onProcessExit = {}
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

    private fun parseNetworkSnapshot(networkStatLine: String): NetworkSnapshot? {
        val networkStatFields = networkStatLine
            .trim()
            .split(Regex("\\s+"))

        if (networkStatFields.size < NETWORK_REQUIRED_FIELD_COUNT) {
            return null
        }

        return try {
            NetworkSnapshot(
                receivedBytes = networkStatFields[0].toLong(),
                transmittedBytes = networkStatFields[1].toLong()
            )
        } catch (_: NumberFormatException) {
            null
        }
    }

    private fun calculateNetworkInterfaceMetrics(
        networkInterfaceName: String,
        networkInterfaceAddress: String,
        firstNetworkSnapshot: NetworkSnapshot,
        secondNetworkSnapshot: NetworkSnapshot
    ): NetworkInterfaceMetrics {
        val currentUploadBytes = calculateDelta(
            firstValue = firstNetworkSnapshot.transmittedBytes,
            secondValue = secondNetworkSnapshot.transmittedBytes
        )
        val currentDownloadBytes = calculateDelta(
            firstValue = firstNetworkSnapshot.receivedBytes,
            secondValue = secondNetworkSnapshot.receivedBytes
        )

        return NetworkInterfaceMetrics(
            interfaceName = networkInterfaceName,
            interfaceIcon = resolveNetworkInterfaceIcon(networkInterfaceName),
            interfaceAddress = networkInterfaceAddress,
            currentUploadRate = currentUploadBytes.toMebibyte(),
            currentDownloadRate = currentDownloadBytes.toMebibyte(),
            totalUploadedMebibytes = secondNetworkSnapshot.transmittedBytes.toMebibyte(),
            totalDownloadedMebibytes = secondNetworkSnapshot.receivedBytes.toMebibyte()
        )
    }

    private fun calculateDelta(
        firstValue: Long,
        secondValue: Long
    ): Long {
        return (secondValue - firstValue).coerceAtLeast(0L)
    }

    private fun findNetworkByTransport(transportType: Int): Network? {
        return connectivityManager.allNetworks.firstOrNull { network ->
            connectivityManager
                .getNetworkCapabilities(network)
                ?.hasTransport(transportType) == true
        }
    }

    private fun findNetworkInterfaceName(transportType: Int): String? {
        val network = findNetworkByTransport(transportType) ?: return null
        return connectivityManager.getLinkProperties(network)?.interfaceName
    }

    private fun resolveNetworkInterfaceIcon(networkInterfaceName: String): Int {
        return when {
            networkInterfaceName.startsWith("rmnet") -> R.drawable.material_symbols_sim_card
            networkInterfaceName.startsWith("ccmni") -> R.drawable.material_symbols_sim_card
            networkInterfaceName.startsWith("tun") -> R.drawable.material_symbols_vpn_key
            else -> R.drawable.material_symbols_wifi
        }
    }

    private fun Long.toMebibyte(): Float {
        return toFloat() / BYTES_PER_MEBIBYTE
    }

    private data class NetworkSnapshot(
        val receivedBytes: Long,
        val transmittedBytes: Long
    )

    private companion object {
        const val DEFAULT_NETWORK_INTERFACE_NAME = "wlan0"
        const val NETWORK_SAMPLE_INTERVAL_MILLIS = 1_000L
        const val NETWORK_READ_TIMEOUT_MILLIS = 3_000L
        const val NETWORK_REQUIRED_FIELD_COUNT = 2
        const val NETWORK_INTERFACE_NOT_FOUND_MARKER = "__NETWORK_INTERFACE_NOT_FOUND__"
        const val BYTES_PER_MEBIBYTE = 1024f
    }
}
