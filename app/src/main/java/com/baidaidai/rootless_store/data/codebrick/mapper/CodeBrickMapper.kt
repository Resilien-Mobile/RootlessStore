package com.baidaidai.rootless_store.data.codebrick.mapper

import com.baidaidai.rootless_store.data.codebrick.database.CodeBrickEntity
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig

object CodeBrickMapper {

    // CodeBrickEntity to CodeBrickConfig
    fun CodeBrickEntity.toCodeBrickConfig(): CodeBrickConfig {
        return CodeBrickConfig(
            unixTimeStamp = unixTimeStamp,
            codeBrickTitle = codeBrickTitle,
            codeBrickEnvironment = codeBrickEnvironment,
            codeBrickContent = codeBrickContent
        )
    }

    // CodeBrickConfig to CodeBrickEntity
    fun CodeBrickConfig.toCodeBrickEntity(): CodeBrickEntity {
        return CodeBrickEntity(
            unixTimeStamp = unixTimeStamp,
            codeBrickTitle = codeBrickTitle,
            codeBrickEnvironment = codeBrickEnvironment,
            codeBrickContent = codeBrickContent
        )
    }


}
