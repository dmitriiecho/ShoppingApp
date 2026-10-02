package krio.systemdesign.shoppingapp

import android.app.UiModeManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import krio.systemdesign.shoppingapp.domain.model.ThemeMode
import krio.systemdesign.shoppingapp.core.ui.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    // Ссылки открывает AppNavHost: так же, как если бы пользователь дошёл до экрана сам.
    private val _deepLinks = Channel<Uri>(Channel.BUFFERED)
    private val deepLinks = _deepLinks.receiveAsFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // После пересоздания активити ссылка уже открыта, её экраны восстановит NavController.
        // А из списка недавних Android запускает приложение с той ссылкой, с которой оно когда-то открылось,
        // хотя пользователь мог давно уйти с её экрана.
        val launchedFromRecents = (intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0
        if (savedInstanceState == null && !launchedFromRecents) {
            intent.data?.let { _deepLinks.trySend(it) }
        }
        // Иначе NavHost при запуске откроет ссылку сам, по правилам Navigation: с каталогом под экраном из ссылки.
        intent.data = null
        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            // Пока тема не прочитана (доли секунды при запуске), ничего не рисуем:
            // иначе экран мелькнёт в теме системы и тут же перекрасится в выбранную.
            val mode = themeMode ?: return@setContent
            val darkTheme = when (mode) {
                ThemeMode.System -> isSystemInDarkTheme()
                ThemeMode.Light -> false
                ThemeMode.Dark -> true
            }
            // Значки строки состояния и панели навигации — под тему приложения, а не системы:
            // иначе при тёмной теме приложения и светлой системе часы и батарея были бы тёмными на тёмном фоне.
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        lightScrim = android.graphics.Color.TRANSPARENT,
                        darkScrim = android.graphics.Color.TRANSPARENT,
                    ) { darkTheme },
                    // Без подложки: фон под кнопками навигации (в режиме трёх кнопок) рисует само приложение,
                    // например панель вкладок продолжается под ними своим цветом.
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim = android.graphics.Color.TRANSPARENT,
                        darkScrim = android.graphics.Color.TRANSPARENT,
                    ) { darkTheme },
                )
                // Иначе система сама подложит под кнопки полупрозрачный фон.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    window.isNavigationBarContrastEnforced = false
                }
                onDispose {}
            }
            LaunchedEffect(mode) { rememberThemeForSplashScreen(mode) }
            ShoppingAppTheme(darkTheme = darkTheme) {
                AppNavHost(
                    deepLinks = deepLinks,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }

    // Стартовый экран Android 12+ рисует система ещё до запуска приложения, по своей теме.
    // Сообщаем ей тему, выбранную в настройках приложения: система её запомнит,
    // и при следующем запуске стартовый экран будет уже в ней.
    private fun rememberThemeForSplashScreen(mode: ThemeMode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
        val nightMode = when (mode) {
            ThemeMode.System -> UiModeManager.MODE_NIGHT_AUTO
            ThemeMode.Light -> UiModeManager.MODE_NIGHT_NO
            ThemeMode.Dark -> UiModeManager.MODE_NIGHT_YES
        }
        getSystemService(UiModeManager::class.java).setApplicationNightMode(nightMode)
    }

    // Ссылка пришла, когда приложение уже открыто (launchMode="singleTop").
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.data?.let { _deepLinks.trySend(it) }
    }
}
