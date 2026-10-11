package krio.systemdesign.shoppingapp.core.network.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import kotlin.time.Duration.Companion.seconds
import krio.systemdesign.shoppingapp.core.config.ServerConfig
import krio.systemdesign.shoppingapp.core.network.HttpClientSetup
import krio.systemdesign.shoppingapp.core.network.serverApi

// Public: the app's graph in :apps:shop skips an internal binding container.
@BindingContainer
@ContributesTo(AppScope::class)
object NetworkModule {
    // Cheap to create: the OkHttp engine builds its own client only at the first request.
    @Provides
    @SingleIn(AppScope::class)
    fun provideHttpClient(setups: Set<HttpClientSetup>): HttpClient = HttpClient(OkHttp) {
        serverApi(ServerConfig.BASE_URL)
        install(HttpTimeout) {
            connectTimeoutMillis = TIMEOUT.inWholeMilliseconds
            socketTimeoutMillis = TIMEOUT.inWholeMilliseconds
        }
        setups.forEach { it.setUp(this) }
    }

    private val TIMEOUT = 30.seconds
}
