package com.baidaidai.rootless_store.domain.assistant.model

data class AssistantResponse(
    val status: AssistantJudgeStatus,
    val shellCode: String?,
)
