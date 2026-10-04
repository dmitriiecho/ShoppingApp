package krio.systemdesign.shoppingapp

import android.app.UiModeManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import krio.systemdesign.shoppingapp.core.ui.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.ui.theme.SystemBarsAppearance
import krio.systemdesign.shoppingapp.domain.model.ThemeMode
import krio.systemdesign.shoppingapp.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    // AppNavHost opens these the way the user would reach the screen.
    private val _deepLinks = Channel<Uri>(Channel.BUFFERED)
    private val deepLinks = _deepLinks.receiveAsFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Not enableEdgeToEdge() from androidx.activity: on a configuration change it reapplies the styles
        // of its first call and brings back the translucent scrim under the navigation buttons.
        WindowCompat.enableEdgeToEdge(window)
        // Skipped after recreation (NavController restores the screens) and from Recents, which relaunches
        // with the original link even if the user has long left its screen.
        val launchedFromRecents = (intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0
        if (savedInstanceState == null && !launchedFromRecents) {
            intent.data?.let { _deepLinks.trySend(it) }
        }
        // Otherwise NavHost would open the link itself, its own way.
        intent.data = null
        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            // Nothing is drawn until the theme is read, or the screen would flash in the system theme.
            val mode = themeMode ?: return@setContent
            val darkTheme = when (mode) {
                ThemeMode.System -> isSystemInDarkTheme()
                ThemeMode.Light -> false
                ThemeMode.Dark -> true
            }
            SystemBarsAppearance(window, darkTheme)
            LaunchedEffect(mode) { rememberThemeForSplashScreen(mode) }
            ShoppingAppTheme(darkTheme = darkTheme) {
                AppNavHost(
                    deepLinks = deepLinks,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }

    // The Android 12+ splash screen is drawn by the system before the app starts. It remembers this mode,
    // so the next splash matches the app's theme.
    private fun rememberThemeForSplashScreen(mode: ThemeMode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
        val nightMode = when (mode) {
            ThemeMode.System -> UiModeManager.MODE_NIGHT_AUTO
            ThemeMode.Light -> UiModeManager.MODE_NIGHT_NO
            ThemeMode.Dark -> UiModeManager.MODE_NIGHT_YES
        }
        getSystemService(UiModeManager::class.java).setApplicationNightMode(nightMode)
    }

    // A link while the app is open (launchMode="singleTop").
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.data?.let { _deepLinks.trySend(it) }
    }
}
