package com.baidaidai.rootless_store.application.codebrick

import com.baidaidai.rootless_store.data.codebrick.repository.CodeBrickRepositoryImpl
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import javax.inject.Inject

class FindCodeBrickUseCase @Inject constructor(
    private val codeBrickRepositoryImpl: CodeBrickRepositoryImpl
) {
    suspend operator fun invoke(
        unixTimeStamp: Long
    ): CodeBrickConfig? = codeBrickRepositoryImpl.findCodeBrickConfig(unixTimeStamp)
}
