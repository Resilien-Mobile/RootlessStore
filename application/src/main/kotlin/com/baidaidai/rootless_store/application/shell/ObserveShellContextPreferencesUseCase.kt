package com.baidaidai.rootless_store.application.shell

import com.baidaidai.rootless_store.domain.shell.repository.ShellPreferencesRepository
import com.baidaidai.rootless_store.domain.shell.model.ShellContextPreferences
import javax.inject.Inject

class ObserveShellContextPreferencesUseCase @Inject constructor(
    private val shellPreferencesRepository: ShellPreferencesRepository
) {
    val defaultPreferences = ShellContextPreferences()

    operator fun invoke() = shellPreferencesRepository.observeShellContextPreferences()
}
