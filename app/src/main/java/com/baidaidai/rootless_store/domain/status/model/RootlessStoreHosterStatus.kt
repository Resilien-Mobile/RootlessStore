package com.baidaidai.rootless_store.domain.status.model

data class RootlessStoreHosterStatus(
    val hosterOverallStatus: HosterOverallStatus? = null,
    val osAndApiVersion: AndroidAndApiStatus? = null,
    val kernelVersion: String? = null,
    val seLinuxStatus: SeLinuxStatus = SeLinuxStatus.Unknow,
    val pluginStatus: PluginStatus = PluginStatus(
        enabledCount = 0,
        totalCount = 0
    ),
    val memoryStatus: MemoryStatus = MemoryStatus(),
    val storageStatus: StorageStatus = StorageStatus(),
    val tempStatus: TempStatus? = null,
)