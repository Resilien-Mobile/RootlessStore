package com.baidaidai.rootless_store.application.codebrick

import com.baidaidai.rootless_store.domain.codebrick.repository.CodeBrickRepository
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import javax.inject.Inject

class AddCodeBrickUseCase @Inject constructor(
    private val codeBrickRepository: CodeBrickRepository
) {
    suspend operator fun invoke(
        codeBrickTitle: String,
        codeBrickContent: String,
        codeBrickContext: ExecutionContext,
        boundTileIndex: Int?
    ) {

        val codeBrickConfig = CodeBrickConfig(
            unixTimestamp = System.currentTimeMillis(),
            codeBrickTitle = codeBrickTitle,
            codeBrickContent = codeBrickContent,
            codeBrickEnvironment = codeBrickContext,
            boundTileIndex = boundTileIndex
        )

        codeBrickRepository.addCodeBrick(codeBrickConfig)
    }
}
