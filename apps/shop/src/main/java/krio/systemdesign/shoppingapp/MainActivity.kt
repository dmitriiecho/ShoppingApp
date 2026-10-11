package krio.systemdesign.shoppingapp

import android.app.UiModeManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import krio.systemdesign.shoppingapp.core.composeutils.viewmodel.LocalViewModelFactory
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.designsystem.theme.SystemBarsAppearance
import krio.systemdesign.shoppingapp.navigation.AppNavHost
import krio.systemdesign.shoppingapp.shared.domain.model.ThemeMode

class MainActivity : ComponentActivity() {

    private val viewModelFactory by lazy { (application as ShoppingApp).graph.viewModelFactory }

    private val viewModel: MainViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Not enableEdgeToEdge() from androidx.activity: on a configuration change it reapplies the styles
        // of its first call and brings back the translucent scrim under the navigation buttons.
        WindowCompat.enableEdgeToEdge(window)
        takeLaunchDeepLink(savedInstanceState)
        setContent {
            // Screens get their ViewModels with injectedViewModel(), which creates them with this factory from
            // the app's graph: each ViewModel gets its dependencies and its own SavedStateHandle.
            CompositionLocalProvider(LocalViewModelFactory provides viewModelFactory) { ShoppingAppContent() }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.data?.let(viewModel::openDeepLink)
    }

    // Opens the link the app was launched with, but not after recreation: the restored screens already show it.
    // And not from Recents: Android relaunches with the old intent, so its link would open again.
    private fun takeLaunchDeepLink(savedInstanceState: Bundle?) {
        val launchedFromRecents = (intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0
        if (savedInstanceState == null && !launchedFromRecents) {
            intent.data?.let(viewModel::openDeepLink)
        }
        // Cleared so NavHost doesn't also open it on its own, with a different back stack.
        intent.data = null
    }

    @Composable
    private fun ShoppingAppContent() {
        val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
        // Nothing is drawn until the theme is read, or the screen would flash in the system theme.
        val mode = themeMode ?: return
        val darkTheme = mode.isDark()
        SystemBarsAppearance(window, darkTheme)
        LaunchedEffect(mode) { setSplashScreenTheme(mode) }
        ShoppingAppTheme(darkTheme = darkTheme) {
            AppNavHost(
                deepLinks = viewModel.deepLinks,
                onScreenView = viewModel::onScreenView,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }

    // The Android 12+ splash screen is drawn by the system before the app starts. It remembers this mode,
    // so the next splash matches the app's theme.
    private fun setSplashScreenTheme(mode: ThemeMode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
        val nightMode = when (mode) {
            ThemeMode.System -> UiModeManager.MODE_NIGHT_AUTO
            ThemeMode.Light -> UiModeManager.MODE_NIGHT_NO
            ThemeMode.Dark -> UiModeManager.MODE_NIGHT_YES
        }
        getSystemService(UiModeManager::class.java).setApplicationNightMode(nightMode)
    }
}

@Composable
@ReadOnlyComposable
private fun ThemeMode.isDark(): Boolean = when (this) {
    ThemeMode.System -> isSystemInDarkTheme()
    ThemeMode.Light -> false
    ThemeMode.Dark -> true
}
