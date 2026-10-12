package krio.systemdesign.shoppingapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.uikit.LocalUIViewController
import krio.systemdesign.shoppingapp.crossfeature.domain.model.ThemeMode
import platform.UIKit.UIUserInterfaceStyle

// The window takes the app's theme, and everything in it follows: the status bar, the keyboard, the background
// behind Compose. Compose content is composed only while its view is in a window, so the window is there.
@Composable
internal actual fun PlatformThemeEffect(
    themeMode: ThemeMode,
    darkTheme: Boolean,
) {
    val window = LocalUIViewController.current.view.window
    LaunchedEffect(window, themeMode) {
        window?.overrideUserInterfaceStyle = when (themeMode) {
            ThemeMode.System -> UIUserInterfaceStyle.UIUserInterfaceStyleUnspecified
            ThemeMode.Light -> UIUserInterfaceStyle.UIUserInterfaceStyleLight
            ThemeMode.Dark -> UIUserInterfaceStyle.UIUserInterfaceStyleDark
        }
    }
}
