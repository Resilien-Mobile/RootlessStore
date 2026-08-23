package com.baidaidai.rootless_store.data.codebrick.mapper

import com.baidaidai.rootless_store.data.codebrick.database.CodeBrickEntity
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig

object CodeBrickMapper {

    // CodeBrickEntity to CodeBrickConfig
    fun CodeBrickEntity.toCodeBrickConfig(): CodeBrickConfig {
        return CodeBrickConfig(
            unixTimestamp = unixTimestamp,
            codeBrickTitle = codeBrickTitle,
            codeBrickEnvironment = codeBrickEnvironment,
            codeBrickContent = codeBrickContent,
            boundTileIndex = boundTileIndex
        )
    }

    // CodeBrickConfig to CodeBrickEntity
    fun CodeBrickConfig.toCodeBrickEntity(): CodeBrickEntity {
        return CodeBrickEntity(
            unixTimestamp = unixTimestamp,
            codeBrickTitle = codeBrickTitle,
            codeBrickEnvironment = codeBrickEnvironment,
            codeBrickContent = codeBrickContent,
            boundTileIndex = boundTileIndex
        )
    }


}
