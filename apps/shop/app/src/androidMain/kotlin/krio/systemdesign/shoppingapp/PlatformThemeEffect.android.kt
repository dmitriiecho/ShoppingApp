package krio.systemdesign.shoppingapp

import android.app.UiModeManager
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import krio.systemdesign.shoppingapp.core.designsystem.theme.SystemBarsAppearance
import krio.systemdesign.shoppingapp.crossfeature.domain.model.ThemeMode

@Composable
internal actual fun PlatformThemeEffect(
    themeMode: ThemeMode,
    darkTheme: Boolean,
) {
    val activity = LocalActivity.current ?: return
    SystemBarsAppearance(activity.window, darkTheme)
    LaunchedEffect(themeMode) { activity.getSystemService(UiModeManager::class.java).setSplashScreenTheme(themeMode) }
}

// The Android 12+ splash screen is drawn by the system before the app starts. It remembers this mode,
// so the next splash matches the app's theme.
private fun UiModeManager.setSplashScreenTheme(mode: ThemeMode) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
    val nightMode = when (mode) {
        ThemeMode.System -> UiModeManager.MODE_NIGHT_AUTO
        ThemeMode.Light -> UiModeManager.MODE_NIGHT_NO
        ThemeMode.Dark -> UiModeManager.MODE_NIGHT_YES
    }
    setApplicationNightMode(nightMode)
}
