package krio.systemdesign.shoppingapp

import androidx.compose.ui.window.ComposeUIViewController
import co.touchlab.kermit.Logger
import dev.zacsweers.metro.createGraph
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform
import platform.UIKit.UIViewController

// The iOS app's entry points, called from Swift (apps/shop/ios).

// The screen: the Swift side shows this controller full screen.
@Suppress("FunctionName", "unused")
fun MainViewController(): UIViewController = ComposeUIViewController { App(IosApp.graph) }

// A link the app is asked to open (SwiftUI's onOpenURL), at a cold start too.
@Suppress("unused")
fun openDeepLink(link: String) {
    IosApp.graph.incomingDeepLinks.open(link)
}

// What lives as long as the app: set up once, by whichever comes first, the screen or a link.
private object IosApp {
    init {
        Logger.setTag("ShoppingApp")
        // Kermit writes to the system log from the start. Logs hold user data, so a release build writes nowhere.
        @OptIn(ExperimentalNativeApi::class)
        if (!Platform.isDebugBinary) {
            Logger.setLogWriters(emptyList())
        }
    }

    val graph: IosAppGraph = createGraph()
}
