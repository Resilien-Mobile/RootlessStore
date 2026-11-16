package com.baidaidai.rootless_store.data.assistant.mapper

import com.baidaidai.rootless_store.data.assistant.api.dto.AssistantDto
import com.baidaidai.rootless_store.domain.assistant.model.AssistantResponse

object LlmMapper {

    fun AssistantDto.toShellCommand(): AssistantResponse {
        return AssistantResponse(
            status = assistantJudgeStatus,
            shellCode = shellCode
        )
    }

}
