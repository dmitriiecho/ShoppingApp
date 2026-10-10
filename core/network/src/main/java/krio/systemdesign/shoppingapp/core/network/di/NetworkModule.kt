package krio.systemdesign.shoppingapp.core.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds
import krio.systemdesign.shoppingapp.core.config.ServerConfig
import krio.systemdesign.shoppingapp.core.network.HttpClientSetup
import krio.systemdesign.shoppingapp.core.network.serverApi

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    // Cheap to create: the OkHttp engine builds its own client only at the first request.
    @Provides
    @Singleton
    fun provideHttpClient(setups: Set<@JvmSuppressWildcards HttpClientSetup>): HttpClient = HttpClient(OkHttp) {
        serverApi(ServerConfig.BASE_URL)
        install(HttpTimeout) {
            connectTimeoutMillis = TIMEOUT.inWholeMilliseconds
            socketTimeoutMillis = TIMEOUT.inWholeMilliseconds
        }
        setups.forEach { it.setUp(this) }
    }

    private val TIMEOUT = 30.seconds
}
