package com.baidaidai.rootless_store.application.codebrick

import com.baidaidai.rootless_store.data.codebrick.repository.CodeBrickRepositoryImpl
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import javax.inject.Inject

class UpdateOneCodeBrickUseCase @Inject constructor(
    private val codeBrickRepositoryImpl: CodeBrickRepositoryImpl
) {
    suspend operator fun invoke(
        codeBrickTitle: String,
        codeBrickContent: String,
        codeBrickContext: HosterOverallStatus,
        bindTileIndex: Int?,
        oldCodeBrickConfig: CodeBrickConfig
    ) {

        val codeBrickConfig = CodeBrickConfig(
            unixTimeStamp = oldCodeBrickConfig.unixTimeStamp,
            codeBrickTitle = codeBrickTitle,
            codeBrickContent = codeBrickContent,
            codeBrickEnvironment = codeBrickContext,
            bindTileIndex = bindTileIndex
        )

        codeBrickRepositoryImpl.updateOneCodeBrickConfig(codeBrickConfig)
    }
}
