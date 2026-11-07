package com.baidaidai.rootless_store.application.codebrick

import com.baidaidai.rootless_store.domain.codebrick.repository.CodeBrickRepository
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import javax.inject.Inject

class FindCodeBrickByTileIndexUseCase @Inject constructor(
    private val codeBrickRepository: CodeBrickRepository
) {
    suspend operator fun invoke(tileIndex: Int): CodeBrickConfig? = codeBrickRepository.findCodeBrickByTileIndex(tileIndex)
}
