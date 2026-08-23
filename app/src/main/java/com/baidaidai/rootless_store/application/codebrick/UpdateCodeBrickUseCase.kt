package com.baidaidai.rootless_store.application.codebrick

import com.baidaidai.rootless_store.data.codebrick.repository.CodeBrickRepositoryImpl
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import javax.inject.Inject

class UpdateCodeBrickUseCase @Inject constructor(
    private val codeBrickRepositoryImpl: CodeBrickRepositoryImpl
) {
    suspend operator fun invoke(
        codeBrickTitle: String,
        codeBrickContent: String,
        codeBrickContext: HosterOverallStatus,
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

        codeBrickRepositoryImpl.updateCodeBrick(codeBrickConfig)
    }
}
