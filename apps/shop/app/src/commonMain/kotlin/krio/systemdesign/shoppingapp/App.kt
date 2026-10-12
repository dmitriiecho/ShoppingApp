package krio.systemdesign.shoppingapp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import krio.systemdesign.shoppingapp.core.composeutils.viewmodel.LocalViewModelFactory
import krio.systemdesign.shoppingapp.core.composeutils.viewmodel.injectedViewModel
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.crossfeature.domain.model.ThemeMode
import krio.systemdesign.shoppingapp.navigation.AppNavHost

// The root of the shop's UI on every platform: the platform's entry point (MainActivity, MainViewController)
// shows it with the app's graph.
@Composable
fun App(
    graph: AppGraph,
    modifier: Modifier = Modifier,
) {
    // Coil's loader for every image in the app, set before any image asks for it. Images use the app's client: they
    // share its connections and timeouts, and wait for the request delay from the settings like any other request.
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory(httpClient = { graph.httpClient })) }
            .build()
    }
    // Screens get their ViewModels with injectedViewModel(), which creates them with this factory from the app's
    // graph: each ViewModel gets its dependencies and its own SavedStateHandle.
    CompositionLocalProvider(LocalViewModelFactory provides graph.viewModelFactory) {
        AppContent(graph.incomingDeepLinks, modifier)
    }
}

@Composable
private fun AppContent(
    incomingDeepLinks: IncomingDeepLinks,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = injectedViewModel(),
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    // Nothing is drawn until the theme is read, or the screen would flash in the system theme.
    val mode = themeMode ?: return
    val darkTheme = mode.isDark()
    PlatformThemeEffect(mode, darkTheme)
    ShoppingAppTheme(darkTheme = darkTheme) {
        AppNavHost(
            deepLinks = incomingDeepLinks.links,
            onScreenView = viewModel::onScreenView,
            modifier = modifier.fillMaxSize(),
        )
    }
}

@Composable
@ReadOnlyComposable
private fun ThemeMode.isDark(): Boolean = when (this) {
    ThemeMode.System -> isSystemInDarkTheme()
    ThemeMode.Light -> false
    ThemeMode.Dark -> true
}
