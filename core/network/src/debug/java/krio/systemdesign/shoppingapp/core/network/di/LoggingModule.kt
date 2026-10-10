package krio.systemdesign.shoppingapp.core.network.di

import co.touchlab.kermit.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger as KtorLogger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import krio.systemdesign.shoppingapp.core.network.HttpClientSetup

@Module
@InstallIn(SingletonComponent::class)
internal object LoggingModule {
    // Requests and responses in full, in debug only.
    @Provides
    @IntoSet
    fun provideLogging(): HttpClientSetup = HttpClientSetup { config ->
        config.install(Logging) {
            val logger = Logger.withTag("Network")
            this.logger = object : KtorLogger {
                override fun log(message: String) = logger.i { message }
            }
            level = LogLevel.BODY
            sanitizeHeader { it == HttpHeaders.Authorization || it == HttpHeaders.Cookie }
        }
    }
}
