package krio.systemdesign.shoppingapp

import android.app.Application
import co.touchlab.kermit.Logger
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import dev.zacsweers.metro.createGraphFactory

class ShoppingApp :
    Application(),
    SingletonImageLoader.Factory {

    // The app's dependency graph, created by the first screen or image that needs it.
    val graph: AppGraph by lazy { createGraphFactory<AppGraph.Factory>().create(this) }

    override fun onCreate() {
        super.onCreate()
        Logger.setTag("ShoppingApp")
        // Kermit writes to logcat from the start. Logs hold user data, so release writes nowhere.
        // Release can add other writers here, e.g. a crash reporter.
        if (!BuildConfig.DEBUG) {
            Logger.setLogWriters(emptyList())
        }
    }

    // Coil's loader for every image in the app, created at the first image. Images use the app's client: they share
    // its connections and timeouts, and wait for the request delay from the settings like any other server request.
    override fun newImageLoader(context: PlatformContext): ImageLoader = ImageLoader.Builder(context)
        .components { add(KtorNetworkFetcherFactory(httpClient = { graph.httpClient })) }
        .build()
}
