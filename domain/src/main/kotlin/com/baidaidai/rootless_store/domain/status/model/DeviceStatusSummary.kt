package com.baidaidai.rootless_store.domain.status.model

data class DeviceStatusSummary(
    val executionContext: ExecutionContext? = null,
    val androidPlatformVersion: AndroidPlatformVersion? = null,
    val kernelVersion: String? = null,
    val seLinuxStatus: SeLinuxStatus = SeLinuxStatus.Unknown,
    val pluginCount: PluginCount = PluginCount(
        enabledCount = 0,
        totalCount = 0
    ),
    val memoryStatus: MemoryStatus = MemoryStatus(),
    val storageStatus: StorageStatus = StorageStatus(),
    val temperatureStatus: TemperatureStatus? = null,
)
