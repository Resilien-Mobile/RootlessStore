package com.baidaidai.rootless_store.core.i18n

import android.icu.text.MessageFormat
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import java.util.Locale

fun formatIcu(
    pattern: String,
    locale: Locale,
    args: Map<String, Any> = emptyMap()
): String {
    return MessageFormat(pattern, locale).format(args)
}

@Composable
fun icuString(
    @StringRes id: Int,
    args: Map<String, Any> = emptyMap()
): String {
    val pattern = stringResource(id)
    val locales = LocalConfiguration.current.locales
    val locale = if (locales.size() > 0) locales[0] else Locale.getDefault()

    return formatIcu(
        pattern = pattern,
        locale = locale,
        args = args
    )
}
