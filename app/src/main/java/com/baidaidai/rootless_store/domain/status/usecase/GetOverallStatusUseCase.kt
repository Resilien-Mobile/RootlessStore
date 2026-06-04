package com.baidaidai.rootless_store.data.status.repository

import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class GetOverallStatusUseCase @Inject constructor(
    private val storeStatusRepositoryImpl: StoreStatusRepositoryImpl
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<HosterOverallStatus> {
        return storeStatusRepositoryImpl
            .getEnableChooserPreference()
            .flatMapLatest { enableChooser ->  // newest value
                if (enableChooser) {
                    storeStatusRepositoryImpl.getExecuteContextPreference()
                } else {
                    storeStatusRepositoryImpl.getOverallStatus()
                }
            }
            .distinctUntilChanged()  // remove duplication, prevent jitter
    }
}
