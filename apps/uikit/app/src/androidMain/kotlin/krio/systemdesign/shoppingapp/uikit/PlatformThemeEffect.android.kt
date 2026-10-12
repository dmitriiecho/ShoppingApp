package krio.systemdesign.shoppingapp.uikit

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import krio.systemdesign.shoppingapp.core.designsystem.theme.SystemBarsAppearance

@Composable
internal actual fun PlatformThemeEffect(darkTheme: Boolean) {
    val activity = LocalActivity.current ?: return
    SystemBarsAppearance(activity.window, darkTheme)
}
