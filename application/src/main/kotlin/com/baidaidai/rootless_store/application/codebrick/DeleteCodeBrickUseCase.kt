package com.baidaidai.rootless_store.application.codebrick

import com.baidaidai.rootless_store.data.codebrick.repository.CodeBrickRepositoryImpl
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import javax.inject.Inject

class DeleteCodeBrickUseCase @Inject constructor(
    private val codeBrickRepositoryImpl: CodeBrickRepositoryImpl
) {
    suspend operator fun invoke(
        codeBrickConfig: CodeBrickConfig
    ) {
        codeBrickRepositoryImpl.deleteCodeBrick(codeBrickConfig)
    }
}
