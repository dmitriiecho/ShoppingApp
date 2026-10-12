package krio.systemdesign.shoppingapp.uikit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.uikit.LocalUIViewController
import platform.UIKit.UIUserInterfaceStyle

// The window takes the chosen theme, and everything in it follows: the status bar, the keyboard, the background
// behind Compose. Compose content is composed only while its view is in a window, so the window is there.
@Composable
internal actual fun PlatformThemeEffect(darkTheme: Boolean) {
    val window = LocalUIViewController.current.view.window
    LaunchedEffect(window, darkTheme) {
        window?.overrideUserInterfaceStyle = if (darkTheme) {
            UIUserInterfaceStyle.UIUserInterfaceStyleDark
        } else {
            UIUserInterfaceStyle.UIUserInterfaceStyleLight
        }
    }
}
