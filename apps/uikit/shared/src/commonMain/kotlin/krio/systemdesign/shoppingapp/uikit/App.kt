package krio.systemdesign.shoppingapp.uikit

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.uikit.navigation.UiKitNavHost

// The UI kit: every styled component of the app. It starts in the system theme, and the top bar button switches it,
// so any component can be checked in both themes. The platform's entry point (UiKitActivity, MainViewController) shows it.
@Composable
fun App(modifier: Modifier = Modifier) {
    val isSystemDarkTheme = isSystemInDarkTheme()
    var darkTheme by rememberSaveable { mutableStateOf(isSystemDarkTheme) }
    PlatformThemeEffect(darkTheme)
    ShoppingAppTheme(darkTheme = darkTheme) {
        UiKitNavHost(
            darkTheme = darkTheme,
            onToggleTheme = { darkTheme = !darkTheme },
            modifier = modifier.fillMaxSize(),
        )
    }
}
