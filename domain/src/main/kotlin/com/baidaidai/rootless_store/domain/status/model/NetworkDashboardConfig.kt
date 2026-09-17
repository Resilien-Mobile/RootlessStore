package com.baidaidai.rootless_store.domain.status.model

data class NetworkDashboardConfig(
    val currentUploadRate: Float,
    val currentDownloadRate: Float,
    val networkInterfaces: List<NetworkInterfaceMetrics>
) {
    companion object {
        val _testOnly_ = NetworkDashboardConfig(
            currentUploadRate = 12.5f,
            currentDownloadRate = 86.4f,
            networkInterfaces = listOf(
                NetworkInterfaceMetrics(
                    interfaceName = "rmnet_data0",
                    interfaceType = NetworkInterfaceType.Cellular,
                    interfaceAddress = "172.198.210.132",
                    currentUploadRate = 7.7f,
                    currentDownloadRate = 37.8f,
                    totalUploadedMebibytes = 86.2f,
                    totalDownloadedMebibytes = 1024.7f
                ),
                NetworkInterfaceMetrics(
                    interfaceName = "wlan0",
                    interfaceType = NetworkInterfaceType.Wifi,
                    interfaceAddress = "192.168.1.100",
                    currentUploadRate = 4.8f,
                    currentDownloadRate = 48.6f,
                    totalUploadedMebibytes = 124.6f,
                    totalDownloadedMebibytes = 2048.3f
                )
            )
        )
    }
}

data class NetworkInterfaceMetrics(
    val interfaceName: String,
    val interfaceType: NetworkInterfaceType,
    val interfaceAddress: String,
    val currentUploadRate: Float,
    val currentDownloadRate: Float,
    val totalUploadedMebibytes: Float,
    val totalDownloadedMebibytes: Float
)

enum class NetworkInterfaceType {
    Wifi,
    Cellular,
    Vpn,
    Unknown
}
