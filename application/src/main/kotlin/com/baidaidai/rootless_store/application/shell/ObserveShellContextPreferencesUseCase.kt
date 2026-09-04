package com.baidaidai.rootless_store.application.shell

import com.baidaidai.rootless_store.data.shell.repository.ShellPreferencesRepositoryImpl
import com.baidaidai.rootless_store.domain.shell.model.ShellContextPreferences
import javax.inject.Inject

class ObserveShellContextPreferencesUseCase @Inject constructor(
    private val shellPreferencesRepositoryImpl: ShellPreferencesRepositoryImpl
) {
    val defaultPreferences = ShellContextPreferences()

    operator fun invoke() = shellPreferencesRepositoryImpl.observeShellContextPreferences()
}
