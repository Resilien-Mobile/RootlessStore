package com.baidaidai.illusioncube.application.prop

import com.fasterxml.jackson.databind.ObjectMapper
import java.util.Properties

class EncodePropAsJsonUseCase(
    private val objectMapper: ObjectMapper = ObjectMapper()
) {
    operator fun invoke(targetContent: String): String {
        // Get prop object
        val properties = Properties().apply {
            targetContent
                .reader()
                .use{
                    load(it)
                }
        }

        // Convert prop object to map
        val propertyMap = properties
            .stringPropertyNames()
            .associateWith(properties::getProperty)

        // Return a string
        return objectMapper.writeValueAsString(propertyMap)
    }
}
