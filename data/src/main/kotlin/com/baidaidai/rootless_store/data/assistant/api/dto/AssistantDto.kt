package com.baidaidai.rootless_store.data.assistant.api.dto

import com.baidaidai.rootless_store.domain.assistant.model.AssistantJudgeStatus
import io.ktor.openapi.JsonSchema
import io.ktor.openapi.buildJsonSchema
import kotlinx.serialization.Serializable

@Serializable
data class AssistantDto(
    val assistantJudgeStatus: AssistantJudgeStatus,
    val shellCode: String?,
){

    companion object {

        fun getShellCommandSchema(
        ): JsonSchema =
            serializer()
                .descriptor
                .buildJsonSchema(
                    visiting = mutableSetOf()
                )
    }

}
