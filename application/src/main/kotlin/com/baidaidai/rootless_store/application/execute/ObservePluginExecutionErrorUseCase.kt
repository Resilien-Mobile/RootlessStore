package com.baidaidai.rootless_store.application.execute

import com.baidaidai.rootless_store.data.monitor.PluginProcessMonitor
import javax.inject.Inject


class ObservePluginExecutionErrorUseCase @Inject constructor(
    private val pluginProcessMonitor: PluginProcessMonitor
) {
    operator fun invoke() = pluginProcessMonitor.unexpectedExitNotificationPoster
}