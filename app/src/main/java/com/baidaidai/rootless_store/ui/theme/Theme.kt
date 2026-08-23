package com.baidaidai.rootless_store.ui.theme
import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.baidaidai.rootless_store.ui.theme.*

private val lightScheme = lightColorScheme(
    primary = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.primaryLight,
    onPrimary = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onPrimaryLight,
    primaryContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.primaryContainerLight,
    onPrimaryContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onPrimaryContainerLight,
    secondary = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.secondaryLight,
    onSecondary = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onSecondaryLight,
    secondaryContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.secondaryContainerLight,
    onSecondaryContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onSecondaryContainerLight,
    tertiary = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.tertiaryLight,
    onTertiary = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onTertiaryLight,
    tertiaryContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.tertiaryContainerLight,
    onTertiaryContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onTertiaryContainerLight,
    error = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.errorLight,
    onError = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onErrorLight,
    errorContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.errorContainerLight,
    onErrorContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onErrorContainerLight,
    background = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.backgroundLight,
    onBackground = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onBackgroundLight,
    surface = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceLight,
    onSurface = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onSurfaceLight,
    surfaceVariant = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceVariantLight,
    onSurfaceVariant = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onSurfaceVariantLight,
    outline = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.outlineLight,
    outlineVariant = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.outlineVariantLight,
    scrim = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.scrimLight,
    inverseSurface = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.inverseSurfaceLight,
    inverseOnSurface = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.inverseOnSurfaceLight,
    inversePrimary = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.inversePrimaryLight,
    surfaceDim = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceDimLight,
    surfaceBright = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceBrightLight,
    surfaceContainerLowest = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceContainerLowestLight,
    surfaceContainerLow = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceContainerLowLight,
    surfaceContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceContainerLight,
    surfaceContainerHigh = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceContainerHighLight,
    surfaceContainerHighest = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.primaryDark,
    onPrimary = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onPrimaryDark,
    primaryContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.primaryContainerDark,
    onPrimaryContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onPrimaryContainerDark,
    secondary = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.secondaryDark,
    onSecondary = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onSecondaryDark,
    secondaryContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.secondaryContainerDark,
    onSecondaryContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onSecondaryContainerDark,
    tertiary = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.tertiaryDark,
    onTertiary = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onTertiaryDark,
    tertiaryContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.tertiaryContainerDark,
    onTertiaryContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onTertiaryContainerDark,
    error = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.errorDark,
    onError = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onErrorDark,
    errorContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.errorContainerDark,
    onErrorContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onErrorContainerDark,
    background = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.backgroundDark,
    onBackground = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onBackgroundDark,
    surface = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceDark,
    onSurface = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onSurfaceDark,
    surfaceVariant = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceVariantDark,
    onSurfaceVariant = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onSurfaceVariantDark,
    outline = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.outlineDark,
    outlineVariant = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.outlineVariantDark,
    scrim = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.scrimDark,
    inverseSurface = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.inverseSurfaceDark,
    inverseOnSurface = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.inverseOnSurfaceDark,
    inversePrimary = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.inversePrimaryDark,
    surfaceDim = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceDimDark,
    surfaceBright = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceBrightDark,
    surfaceContainerLowest = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceContainerLowestDark,
    surfaceContainerLow = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceContainerLowDark,
    surfaceContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceContainerDark,
    surfaceContainerHigh = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceContainerHighDark,
    surfaceContainerHighest = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceContainerHighestDark,
)

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.primaryLightMediumContrast,
    onPrimary = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onPrimaryLightMediumContrast,
    primaryContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.primaryContainerLightMediumContrast,
    onPrimaryContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onPrimaryContainerLightMediumContrast,
    secondary = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.secondaryLightMediumContrast,
    onSecondary = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onSecondaryLightMediumContrast,
    secondaryContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.secondaryContainerLightMediumContrast,
    onSecondaryContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onSecondaryContainerLightMediumContrast,
    tertiary = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.tertiaryLightMediumContrast,
    onTertiary = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onTertiaryLightMediumContrast,
    tertiaryContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.tertiaryContainerLightMediumContrast,
    onTertiaryContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onTertiaryContainerLightMediumContrast,
    error = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.errorLightMediumContrast,
    onError = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onErrorLightMediumContrast,
    errorContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.errorContainerLightMediumContrast,
    onErrorContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onErrorContainerLightMediumContrast,
    background = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.backgroundLightMediumContrast,
    onBackground = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onBackgroundLightMediumContrast,
    surface = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceLightMediumContrast,
    onSurface = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onSurfaceLightMediumContrast,
    surfaceVariant = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceVariantLightMediumContrast,
    onSurfaceVariant = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onSurfaceVariantLightMediumContrast,
    outline = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.outlineLightMediumContrast,
    outlineVariant = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.outlineVariantLightMediumContrast,
    scrim = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.scrimLightMediumContrast,
    inverseSurface = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.inverseSurfaceLightMediumContrast,
    inverseOnSurface = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.inverseOnSurfaceLightMediumContrast,
    inversePrimary = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.inversePrimaryLightMediumContrast,
    surfaceDim = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceDimLightMediumContrast,
    surfaceBright = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceBrightLightMediumContrast,
    surfaceContainerLowest = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceContainerLowLightMediumContrast,
    surfaceContainer = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceContainerLightMediumContrast,
    surfaceContainerHigh = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.surfaceContainerHighestLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.primaryLightHighContrast,
    onPrimary = _root_ide_package_.com.baidaidai.rootless_store.ui.theme.onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)

@Composable
@ExperimentalMaterial3ExpressiveApi
fun RootlessStoreTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    isDynamicColorEnabled: Boolean = true,
    content: @Composable() () -> Unit
) {
  val colorScheme = when {
      isDynamicColorEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
          val context = LocalContext.current
          if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      isDarkTheme -> darkScheme
      else -> lightScheme
  }
    MaterialExpressiveTheme(
    colorScheme = colorScheme,
    typography = AppTypography,
    content = content
  )
}

