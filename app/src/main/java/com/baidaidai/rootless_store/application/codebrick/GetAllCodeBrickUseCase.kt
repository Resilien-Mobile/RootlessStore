package com.baidaidai.rootless_store.application.codebrick

import com.baidaidai.rootless_store.data.codebrick.repository.CodeBrickRepositoryImpl
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCodeBrickUseCase @Inject constructor(
    private val codeBrickRepositoryImpl: CodeBrickRepositoryImpl
) {
    operator fun invoke(): Flow<List<CodeBrickConfig>> = codeBrickRepositoryImpl.getAllCodeBrickConfig()
}
