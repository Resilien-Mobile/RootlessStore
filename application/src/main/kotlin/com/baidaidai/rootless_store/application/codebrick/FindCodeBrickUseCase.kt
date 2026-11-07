package com.baidaidai.rootless_store.application.codebrick

import com.baidaidai.rootless_store.domain.codebrick.repository.CodeBrickRepository
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import javax.inject.Inject

class FindCodeBrickUseCase @Inject constructor(
    private val codeBrickRepository: CodeBrickRepository
) {
    suspend operator fun invoke(
        unixTimestamp: Long
    ): CodeBrickConfig? = codeBrickRepository.findCodeBrick(unixTimestamp)
}
