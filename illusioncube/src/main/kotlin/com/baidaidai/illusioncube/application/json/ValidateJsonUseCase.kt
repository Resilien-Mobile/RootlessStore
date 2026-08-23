package com.baidaidai.illusioncube.application.json

import com.fasterxml.jackson.databind.ObjectMapper

class ValidateJsonUseCase(
    private val objectMapper: ObjectMapper = ObjectMapper()
) {

    operator fun invoke(targetContent: String): Boolean {
        if (targetContent.isEmpty()) return false

        try {
            objectMapper.readTree(targetContent)
            return true
        }catch (error: Throwable){
            return false
        }
    }

}
