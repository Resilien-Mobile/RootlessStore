package com.baidaidai.rootless_store.domain.assistant.model

import kotlinx.serialization.Serializable

@Serializable
enum class AssistantJudgeStatus {
    legal,
    illegal,
}
