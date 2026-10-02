package krio.systemdesign.shoppingapp.data.network

import krio.systemdesign.shoppingapp.domain.repository.AppSettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

// Перед каждым запросом к серверу ждёт столько, сколько выбрано в настройках («Задержка запросов»).
class NetworkDelayInterceptor @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // Перехватчик работает в потоке OkHttp, а не в главном, поэтому подождать чтения настройки здесь можно.
        // DataStore держит прочитанные настройки в памяти, так что файл читается только при первом запросе.
        val delay = runBlocking { appSettingsRepository.observeNetworkDelay().first() }
        var remainingMillis = delay.duration.inWholeMilliseconds
        // Ждём частями, чтобы запрос, который отменили (например, ушли с экрана), не висел до конца паузы.
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
