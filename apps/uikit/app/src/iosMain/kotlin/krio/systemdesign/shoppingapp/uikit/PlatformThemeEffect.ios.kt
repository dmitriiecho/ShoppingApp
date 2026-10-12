package krio.systemdesign.shoppingapp.uikit

import androidx.compose.runtime.Composable

// The iOS app shows App() from its next step on; until then there is nothing to theme.
@Composable
internal actual fun PlatformThemeEffect(darkTheme: Boolean) = Unit
