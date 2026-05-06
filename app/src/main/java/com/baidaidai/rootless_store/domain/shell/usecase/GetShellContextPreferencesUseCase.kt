package com.baidaidai.rootless_store.domain.shell.usecase

import com.baidaidai.rootless_store.data.shell.repository.ShellContextPreferences
import com.baidaidai.rootless_store.data.shell.repository.ShellPreferencesRepositoryImpl
import javax.inject.Inject

class GetShellContextPreferencesUseCase @Inject constructor(
    private val shellPreferencesRepositoryImpl: ShellPreferencesRepositoryImpl
) {
    val defaultPreferences = ShellContextPreferences()

    operator fun invoke() = shellPreferencesRepositoryImpl.shellContextPreferences
}
