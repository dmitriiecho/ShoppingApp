package krio.systemdesign.shoppingapp.core.ui.theme

import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.core.view.WindowCompat

// System bar icons follow the app's theme, not the system's: with a dark app theme on a light system
// the clock and battery would otherwise be dark on dark.
@Composable
fun SystemBarsAppearance(window: Window, darkTheme: Boolean) {
    DisposableEffect(window, darkTheme) {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = !darkTheme
            isAppearanceLightNavigationBars = !darkTheme
        }
        onDispose {}
    }
}
