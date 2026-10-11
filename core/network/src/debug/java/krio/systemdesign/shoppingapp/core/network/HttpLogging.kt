package krio.systemdesign.shoppingapp.core.network

import co.touchlab.kermit.Logger
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger as KtorLogger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders

// Requests and responses in full, in debug only.
@ContributesIntoSet(AppScope::class)
internal class HttpLogging : HttpClientSetup {
    override fun setUp(config: HttpClientConfig<*>) {
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
