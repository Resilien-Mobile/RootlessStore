package com.baidaidai.rootless_store.application.status

import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.status.model.StorageStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveStorageStatusUseCase @Inject constructor(
    private val storeStatusRepositoryImpl: StoreStatusRepositoryImpl
){
    operator fun invoke(): Flow<StorageStatus> {
        return storeStatusRepositoryImpl.observeStorageStatus()
    }
}
