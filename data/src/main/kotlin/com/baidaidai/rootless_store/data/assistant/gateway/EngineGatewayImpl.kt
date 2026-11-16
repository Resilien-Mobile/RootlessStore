package com.baidaidai.rootless_store.data.assistant.gateway

import com.baidaidai.rootless_store.data.assistant.model.EngineManager
import com.baidaidai.rootless_store.domain.assistant.error.AssistantError
import com.baidaidai.rootless_store.domain.assistant.model.AssistantEngineStatus
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EngineGatewayImpl @Inject constructor(
    private val engineManager: EngineManager
){

    fun observeEngineStatus(): Flow<AssistantEngineStatus> = engineManager.observeEngineStatus()

    @Synchronized
    fun closeAssistantEngine() {
        engineManager.closeLlmEngine()
    }

    @Synchronized
    fun initializeAssistantEngine(): Result<Unit, AssistantError> = engineManager.initializeLlmEngine()

}
