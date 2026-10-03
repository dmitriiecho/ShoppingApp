package krio.systemdesign.shoppingapp.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

@Composable
fun ShoppingAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalDarkTheme provides darkTheme,
        LocalAppColors provides if (darkTheme) DarkAppColors else LightAppColors,
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content,
        )
    }
}

// What the app theme adds to MaterialTheme.
object ShoppingAppTheme {
    // Whether the theme is dark, e.g. ProductImage picks its "no image" placeholder by it.
    val isDark: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalDarkTheme.current

    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current
}

// The theme is switched in the app settings, so the system night mode
// (isSystemInDarkTheme, -night resources) may not match it.
private val LocalDarkTheme = staticCompositionLocalOf { false }

// Without ShoppingAppTheme above (e.g. a preview or test that doesn't wrap in it) — the light set.
private val LocalAppColors = staticCompositionLocalOf { LightAppColors }
