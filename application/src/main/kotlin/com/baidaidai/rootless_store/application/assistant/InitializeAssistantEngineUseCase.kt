package com.baidaidai.rootless_store.application.assistant

import com.baidaidai.rootless_store.data.assistant.gateway.EngineGatewayImpl
import com.baidaidai.rootless_store.domain.assistant.error.AssistantError
import com.github.michaelbull.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InitializeAssistantEngineUseCase @Inject constructor(
    private val engineGatewayImpl: EngineGatewayImpl
) {

    suspend operator fun invoke(): Result<Unit, AssistantError> = withContext(Dispatchers.IO) {
        engineGatewayImpl.initializeAssistantEngine()
    }

}
