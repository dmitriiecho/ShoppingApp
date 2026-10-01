package krio.systemdesign.shoppingapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import krio.systemdesign.shoppingapp.core.ui.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

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
            ShoppingAppTheme {
                AppNavHost(
                    deepLinks = deepLinks,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }

    // Ссылка пришла, когда приложение уже открыто (launchMode="singleTop").
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.data?.let { _deepLinks.trySend(it) }
    }
}
