//package com.baidaidai.rootless_store.domain.status.usecase
//
//import com.baidaidai.rootless_store.data.status.repository.GetOverallStatusUseCase
//import com.baidaidai.rootless_store.domain.status.model.RootlessStoreHosterStatus
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.combine
//import javax.inject.Inject
//
//class GetRootlessStoreHosterStatusUseCase @Inject constructor(
//    private val getMemoryStatusUseCase: GetMemoryStatusUseCase,
//    private val getStorageStatusUseCase: GetStorageStatusUseCase,
//    private val getPluginStatusUseCase: GetPluginStatusUseCase,
//    private val getSELinuxUseCase: GetSELinuxUseCase,
//    private val getKernelStatusUseCase: GetKernelStatusUseCase,
//    private val getTemperatureStatusUseCase: GetTemperatureStatusUseCase,
//    private val getAndroidAndAPIStatusUseCase: GetAndroidAndAPIStatusUseCase,
//    private val getOverallStatusUseCase: GetOverallStatusUseCase
//) {
//    operator fun invoke(): Flow<RootlessStoreHosterStatus> = combine(
//        getMemoryStatusUseCase(),
//        getStorageStatusUseCase(),
//        getPluginStatusUseCase(),
//        getTemperatureStatusUseCase()
//    ) { memoryStatus, storageStatus, pluginStatus, temperatureStatus ->
//        RootlessStoreHosterStatus(
//            hosterOverallStatus = getOverallStatusUseCase(),
//            kernelVersion = getKernelStatusUseCase(),
//            selinuxStatus = getSELinuxUseCase(),
//            pluginStatus = pluginStatus,
//            memoryStatus = memoryStatus,
//            storageStatus = storageStatus,
//            tempStatus = temperatureStatus,
//            osAndAPIVersion = getAndroidAndAPIStatusUseCase()
//        )
//    }
//}
