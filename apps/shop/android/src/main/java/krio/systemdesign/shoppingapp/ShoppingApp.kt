package krio.systemdesign.shoppingapp

import android.app.Application
import co.touchlab.kermit.Logger
import dev.zacsweers.metro.createGraphFactory

class ShoppingApp : Application() {

    // The app's dependency graph, created by the first screen that needs it.
    val graph: AndroidAppGraph by lazy { createGraphFactory<AndroidAppGraph.Factory>().create(this) }

    override fun onCreate() {
        super.onCreate()
        Logger.setTag("ShoppingApp")
        // Kermit writes to logcat from the start. Logs hold user data, so release writes nowhere.
        // Release can add other writers here, e.g. a crash reporter.
        if (!BuildConfig.DEBUG) {
            Logger.setLogWriters(emptyList())
        }
    }
}
