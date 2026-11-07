package com.baidaidai.rootless_store.application.shell

import com.baidaidai.rootless_store.domain.shell.repository.ShellPreferencesRepository
import javax.inject.Inject

class SetShellDirectoryJumpEnabledUseCase @Inject constructor(
    private val shellPreferencesRepository: ShellPreferencesRepository
) {
    suspend operator fun invoke(isEnabled: Boolean) = shellPreferencesRepository.setDirectoryJumpEnabled(isEnabled)
}
