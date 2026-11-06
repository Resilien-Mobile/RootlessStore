package com.baidaidai.rootless_store.application.version

import com.baidaidai.rootless_store.data.update.gateway.StoreUpdateGatewayImpl
import javax.inject.Inject

class FetchLatestVersionTagUseCase @Inject constructor(
    private val storeUpdateGatewayImpl: StoreUpdateGatewayImpl,
) {
    suspend operator fun invoke(): Result<String> {

        return storeUpdateGatewayImpl.fetchLatestVersionTag()

    }
}
