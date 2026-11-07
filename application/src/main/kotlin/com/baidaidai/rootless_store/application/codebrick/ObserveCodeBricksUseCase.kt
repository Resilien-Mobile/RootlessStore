package com.baidaidai.rootless_store.application.codebrick

import com.baidaidai.rootless_store.domain.codebrick.repository.CodeBrickRepository
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCodeBricksUseCase @Inject constructor(
    private val codeBrickRepository: CodeBrickRepository
) {
    operator fun invoke(): Flow<List<CodeBrickConfig>> = codeBrickRepository.observeCodeBricks()
}
