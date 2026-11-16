package com.baidaidai.rootless_store.application.assistant

import com.baidaidai.rootless_store.data.assistant.gateway.EngineGatewayImpl
import com.baidaidai.rootless_store.domain.assistant.model.AssistantEngineStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAssistantEngineStatusUseCase @Inject constructor(
    private val engineGatewayImpl: EngineGatewayImpl
) {

    operator fun invoke(): Flow<AssistantEngineStatus> = engineGatewayImpl.observeEngineStatus()

}
