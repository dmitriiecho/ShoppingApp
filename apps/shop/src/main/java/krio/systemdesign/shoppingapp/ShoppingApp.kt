package krio.systemdesign.shoppingapp

import android.app.Application
import co.touchlab.kermit.Logger
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import dagger.Lazy
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import okhttp3.OkHttpClient

@HiltAndroidApp
class ShoppingApp :
    Application(),
    SingletonImageLoader.Factory {

    // The app's client: images share its connections and timeouts, and wait for the request delay from
    // the settings like any other server request. Lazy: built only when the first request needs it.
    @Inject
    lateinit var okHttpClient: Lazy<OkHttpClient>

    override fun onCreate() {
        super.onCreate()
        Logger.setTag("ShoppingApp")
        // Kermit writes to logcat from the start. Logs hold user data, so release writes nowhere.
        // Release can add other writers here, e.g. a crash reporter.
        if (!BuildConfig.DEBUG) {
            Logger.setLogWriters(emptyList())
        }
    }

    // Coil's loader for every image in the app, created at the first image.
    override fun newImageLoader(context: PlatformContext): ImageLoader = ImageLoader.Builder(context)
        .components { add(OkHttpNetworkFetcherFactory(callFactory = { okHttpClient.get() })) }
        .build()
}
