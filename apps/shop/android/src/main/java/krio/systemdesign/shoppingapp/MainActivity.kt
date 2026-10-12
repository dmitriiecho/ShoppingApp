package krio.systemdesign.shoppingapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat

class MainActivity : ComponentActivity() {

    private val graph by lazy { (application as ShoppingApp).graph }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Not enableEdgeToEdge() from androidx.activity: on a configuration change it reapplies the styles
        // of its first call and brings back the translucent scrim under the navigation buttons.
        WindowCompat.enableEdgeToEdge(window)
        takeLaunchDeepLink(savedInstanceState)
        setContent { App(graph) }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.data?.let { graph.incomingDeepLinks.open(it.toString()) }
    }

    // Opens the link the app was launched with, but not after recreation: the restored screens already show it.
    // And not from Recents: Android relaunches with the old intent, so its link would open again.
    private fun takeLaunchDeepLink(savedInstanceState: Bundle?) {
        val launchedFromRecents = (intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0
        if (savedInstanceState == null && !launchedFromRecents) {
            intent.data?.let { graph.incomingDeepLinks.open(it.toString()) }
        }
        // Cleared so NavHost doesn't also open it on its own, with a different back stack.
        intent.data = null
    }
}
