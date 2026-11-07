package com.baidaidai.rootless_store.application.codebrick

import com.baidaidai.rootless_store.domain.codebrick.repository.CodeBrickRepository
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import javax.inject.Inject

class UpdateCodeBrickUseCase @Inject constructor(
    private val codeBrickRepository: CodeBrickRepository
) {
    suspend operator fun invoke(
        codeBrickTitle: String,
        codeBrickContent: String,
        codeBrickContext: ExecutionContext,
        boundTileIndex: Int?,
        oldCodeBrickConfig: CodeBrickConfig
    ) {

        val codeBrickConfig = CodeBrickConfig(
            unixTimestamp = oldCodeBrickConfig.unixTimestamp,
            codeBrickTitle = codeBrickTitle,
            codeBrickContent = codeBrickContent,
            codeBrickEnvironment = codeBrickContext,
            boundTileIndex = boundTileIndex
        )

        codeBrickRepository.updateCodeBrick(codeBrickConfig)
    }
}
