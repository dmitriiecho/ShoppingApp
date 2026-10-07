package krio.systemdesign.shoppingapp.uikit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.designsystem.theme.SystemBarsAppearance
import krio.systemdesign.shoppingapp.uikit.navigation.UiKitNavHost

// The UI kit: every styled component of the app. It starts in the system theme, and the top bar button switches it,
// so any component can be checked in both themes.
class UiKitActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Not enableEdgeToEdge() from androidx.activity: on a configuration change it reapplies the styles
        // of its first call, not the last one.
        WindowCompat.enableEdgeToEdge(window)
        setContent {
            val isSystemDarkTheme = isSystemInDarkTheme()
            var darkTheme by rememberSaveable { mutableStateOf(isSystemDarkTheme) }
            SystemBarsAppearance(window, darkTheme)
            ShoppingAppTheme(darkTheme = darkTheme) {
                UiKitNavHost(
                    darkTheme = darkTheme,
                    onToggleTheme = { darkTheme = !darkTheme },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
