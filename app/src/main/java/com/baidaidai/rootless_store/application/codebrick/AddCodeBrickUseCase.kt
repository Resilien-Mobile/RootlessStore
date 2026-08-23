package com.baidaidai.rootless_store.application.codebrick

import com.baidaidai.rootless_store.data.codebrick.repository.CodeBrickRepositoryImpl
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import javax.inject.Inject

class AddCodeBrickUseCase @Inject constructor(
    private val codeBrickRepositoryImpl: CodeBrickRepositoryImpl
) {
    suspend operator fun invoke(
        codeBrickTitle: String,
        codeBrickContent: String,
        codeBrickContext: HosterOverallStatus,
        bindTileIndex: Int?
    ) {

        val codeBrickConfig = CodeBrickConfig(
            unixTimeStamp = System.currentTimeMillis(),
            codeBrickTitle = codeBrickTitle,
            codeBrickContent = codeBrickContent,
            codeBrickEnvironment = codeBrickContext,
            bindTileIndex = bindTileIndex
        )

        codeBrickRepositoryImpl.createCodeBrickConfig(codeBrickConfig)
    }
}
