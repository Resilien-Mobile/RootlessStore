package com.baidaidai.rootless_store.data.assistant.model

import com.baidaidai.rootless_store.domain.assistant.error.AssistantError
import com.baidaidai.rootless_store.domain.assistant.model.AssistantEngineStatus
import com.github.michaelbull.result.Result
import com.google.ai.edge.litertlm.Engine
import kotlinx.coroutines.flow.Flow

abstract class EngineManager {

    protected abstract var engine: Engine?

    abstract fun initializeLlmEngine(): Result<Unit, AssistantError>

    abstract fun closeLlmEngine()

    abstract fun observeEngineStatus(): Flow<AssistantEngineStatus>

}
