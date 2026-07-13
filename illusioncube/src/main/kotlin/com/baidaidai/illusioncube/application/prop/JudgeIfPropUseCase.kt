package com.baidaidai.illusioncube.application.prop

import java.util.Properties

class JudgeIfPropUseCase {

    operator fun invoke(targetContent: String): Boolean {
        if (targetContent.isEmpty()) return false

        try {

            // Judge If likes YAML
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

