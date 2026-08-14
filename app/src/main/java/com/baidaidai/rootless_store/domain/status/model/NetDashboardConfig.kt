package com.baidaidai.rootless_store.domain.status.model

import com.baidaidai.rootless_store.R

data class NetDashboardConfig(
    val currentUploadRate: Float,
    val currentDownloadRate: Float,
    val port: List<PortInfo>
) {
    companion object {
        val _testOnly_ = NetDashboardConfig(
            currentUploadRate = 12.5f,
            currentDownloadRate = 86.4f,
            port = listOf(
                PortInfo(
                    portName = "rmnet_data0",
                    portIcon = R.drawable.material_symbols_sim_card,
                    portAddress = "172.198.210.132",
                    currentUploadRate = 7.7f,
                    currentDownloadRate = 37.8f,
                    totalUploadPackage = 86.2f,
                    totalDownloadPackage = 1024.7f
                ),
                PortInfo(
                    portName = "wlan0",
                    portIcon = R.drawable.material_symbols_wifi,
                    portAddress = "192.168.1.100",
                    currentUploadRate = 4.8f,
                    currentDownloadRate = 48.6f,
                    totalUploadPackage = 124.6f,
                    totalDownloadPackage = 2048.3f
                )
            )
        )
    }
}

data class PortInfo(
    val portName: String,
    val portIcon: Int,
    val portAddress: String,
    val currentUploadRate: Float,
    val currentDownloadRate: Float,
    val totalUploadPackage: Float,
    val totalDownloadPackage: Float
)
