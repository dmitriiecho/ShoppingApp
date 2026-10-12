package krio.systemdesign.shoppingapp

import androidx.compose.runtime.Composable
import krio.systemdesign.shoppingapp.crossfeature.domain.model.ThemeMode

// The iOS app shows App() from its next step on; until then there is nothing to theme.
@Composable
internal actual fun PlatformThemeEffect(
    themeMode: ThemeMode,
    darkTheme: Boolean,
) = Unit
