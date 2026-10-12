package krio.systemdesign.shoppingapp

import androidx.compose.runtime.Composable
import krio.systemdesign.shoppingapp.crossfeature.domain.model.ThemeMode

// What the platform draws itself takes the app's theme too: the system bars, the splash screen, the iOS keyboard.
@Composable
internal expect fun PlatformThemeEffect(
    themeMode: ThemeMode,
    darkTheme: Boolean,
)
