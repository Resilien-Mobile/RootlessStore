package com.baidaidai.illusioncube.application.prop

import java.util.Properties

class ValidatePropUseCase {

    operator fun invoke(targetContent: String): Boolean {
        if (targetContent.isEmpty()) return false

        try {

            // Reject YAML-like indentation
            val hasIndentedLine = targetContent
                .lineSequence()
                .any { line ->
                    line.isNotBlank() && line.first().isWhitespace()
                }
            if (hasIndentedLine) return false

            // Shadow Constructor of Properties
            val properties = Properties()
            targetContent.reader().use { reader ->
                properties.load(reader)
            }

            return properties.isNotEmpty()
        } catch (error: Throwable) {
            return false
        }
    }

}
