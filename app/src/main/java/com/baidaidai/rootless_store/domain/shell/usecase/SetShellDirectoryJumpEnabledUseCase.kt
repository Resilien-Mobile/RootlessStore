package com.baidaidai.rootless_store.domain.shell.usecase

import com.baidaidai.rootless_store.data.shell.repository.ShellPreferencesRepositoryImpl
import javax.inject.Inject

class SetShellDirectoryJumpEnabledUseCase @Inject constructor(
    private val shellPreferencesRepositoryImpl: ShellPreferencesRepositoryImpl
) {
    suspend operator fun invoke(isEnabled: Boolean) = shellPreferencesRepositoryImpl.setDirectoryJumpEnabled(isEnabled)
}
