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
import krio.systemdesign.shoppingapp.core.ui.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.ui.theme.SystemBarsAppearance
import krio.systemdesign.shoppingapp.uikit.navigation.UiKitNavHost

// Каталог компонентов :core:ui. Тема сначала как в системе, дальше переключается кнопкой в верхней панели:
// так любой компонент можно сразу посмотреть в обеих темах, не трогая настройки телефона.
class UiKitActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Не enableEdgeToEdge() из androidx.activity: при смене конфигурации она заново применяет стили своего
        // первого вызова, а не последнего. Так же устроено в MainActivity основного приложения.
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
