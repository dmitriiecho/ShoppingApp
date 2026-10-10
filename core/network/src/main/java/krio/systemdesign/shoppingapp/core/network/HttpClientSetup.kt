package krio.systemdesign.shoppingapp.core.network

import io.ktor.client.HttpClientConfig

// Adds something to the app's HttpClient from another module: request logging in debug, the request delay from
// the settings (:shared:data). Contributed to a set with @IntoSet.
fun interface HttpClientSetup {
    fun setUp(config: HttpClientConfig<*>)
}
