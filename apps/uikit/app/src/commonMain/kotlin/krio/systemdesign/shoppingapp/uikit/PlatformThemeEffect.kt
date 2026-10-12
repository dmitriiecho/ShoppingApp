package krio.systemdesign.shoppingapp.uikit

import androidx.compose.runtime.Composable

// What the platform draws itself takes the UI kit's theme too: the system bars.
@Composable
internal expect fun PlatformThemeEffect(darkTheme: Boolean)
