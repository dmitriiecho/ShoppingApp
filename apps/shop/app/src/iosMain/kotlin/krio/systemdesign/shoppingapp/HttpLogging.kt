package krio.systemdesign.shoppingapp

import co.touchlab.kermit.Logger
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger as KtorLogger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform
import krio.systemdesign.shoppingapp.core.network.HttpClientSetup

// Requests and responses in full, in a debug build only. The same as src/debug of :apps:shop:android: a KMP module
// has no build variants, so here the check is at run time.
@ContributesIntoSet(AppScope::class)
internal class HttpLogging : HttpClientSetup {
    @OptIn(ExperimentalNativeApi::class)
    override fun setUp(config: HttpClientConfig<*>) {
        if (!Platform.isDebugBinary) return
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
