package com.baidaidai.rootless_store.domain.process.model

data class ProcessInfo(
    val pid: Int,
    val processName: String?,
    val packageName: String?,
    val processType: ProcessType
)

enum class ProcessType {

    LinuxProcess,
    AndroidPackage,
    Unknown

}
