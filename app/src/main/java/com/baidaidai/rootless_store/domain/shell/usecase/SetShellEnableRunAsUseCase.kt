package com.baidaidai.rootless_store.domain.shell.usecase

import com.baidaidai.rootless_store.data.shell.repository.ShellPreferencesRepositoryImpl
import javax.inject.Inject

class SetShellEnableRunAsUseCase @Inject constructor(
    private val shellPreferencesRepositoryImpl: ShellPreferencesRepositoryImpl
) {
    suspend operator fun invoke(enabled: Boolean) = shellPreferencesRepositoryImpl.setEnableRunAs(enabled)
}
