package krio.systemdesign.shoppingapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class ShoppingApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Without a tree Timber drops every log. DebugTree writes to logcat, so debug only: logs hold user data.
        // Release can plant other trees here, e.g. a crash reporter.
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
