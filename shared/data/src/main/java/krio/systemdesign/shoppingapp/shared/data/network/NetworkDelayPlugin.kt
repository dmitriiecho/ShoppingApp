package krio.systemdesign.shoppingapp.shared.data.network

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.api.createClientPlugin
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import krio.systemdesign.shoppingapp.core.network.HttpClientSetup
import krio.systemdesign.shoppingapp.shared.domain.repository.AppSettingsRepository

// Waits before every server request for the delay chosen in the settings.
// ktlint would wrap this long header before the supertype; the project puts one parameter per line instead.
@Suppress("ktlint:standard:class-signature")
internal class NetworkDelayPlugin @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
) : HttpClientSetup {

    // delay suspends the request's coroutine, so a canceled request stops waiting at once.
    private val plugin = createClientPlugin("NetworkDelay") {
        onRequest { _, _ -> delay(appSettingsRepository.observeNetworkDelay().first().duration) }
    }

    override fun setUp(config: HttpClientConfig<*>) {
        config.install(plugin)
    }
}
