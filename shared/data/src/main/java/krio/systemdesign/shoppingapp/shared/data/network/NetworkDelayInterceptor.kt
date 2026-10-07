package krio.systemdesign.shoppingapp.shared.data.network

import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import krio.systemdesign.shoppingapp.shared.domain.repository.AppSettingsRepository
import okhttp3.Interceptor
import okhttp3.Response

// Waits before every server request for the delay chosen in the settings.
// ktlint would wrap this long header before the supertype; the project puts one parameter per line instead.
@Suppress("ktlint:standard:class-signature")
internal class NetworkDelayInterceptor @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // Blocking is fine: this runs on an OkHttp thread, and DataStore reads the file only once.
        val delay = runBlocking { appSettingsRepository.observeNetworkDelay().first() }
        var remainingMillis = delay.duration.inWholeMilliseconds
        // Sleeps in steps so a canceled request doesn't wait out the whole delay.
        while (remainingMillis > 0) {
            if (chain.call().isCanceled()) throw IOException("Canceled")
            val step = minOf(remainingMillis, STEP_MILLIS)
            Thread.sleep(step)
            remainingMillis -= step
        }
        return chain.proceed(chain.request())
    }

    private companion object {
        const val STEP_MILLIS = 100L
    }
}
