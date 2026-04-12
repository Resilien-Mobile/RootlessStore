package com.baidaidai.rootless_store.domain.shell.usecase

import com.baidaidai.rootless_store.data.shell.repository.ExecuteShellRepositoryImpl
import com.baidaidai.rootless_store.domain.shell.model.ShellCommandContainer
import javax.inject.Inject

class RunCommandUseCase @Inject constructor(
    private val executeShellRepositoryImpl: ExecuteShellRepositoryImpl
) {
    operator fun invoke(shellCommandContainer: ShellCommandContainer) = executeShellRepositoryImpl.runCommand(shellCommandContainer)
}