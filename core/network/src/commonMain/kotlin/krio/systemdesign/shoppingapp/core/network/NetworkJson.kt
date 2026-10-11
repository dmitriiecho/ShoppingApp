package krio.systemdesign.shoppingapp.core.network

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

// How the app reads and writes the server's JSON. Public so that tests read it the same way (networkTest).
// The server has the same settings (serverJson in server/.../ServerJson.kt).
val networkJson: Json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    encodeDefaults = true
}

// The client settings the server's API relies on. Public so that tests set up their client the same way (networkTest).
fun HttpClientConfig<*>.serverApi(baseUrl: String) {
    defaultRequest { url(baseUrl) }
    install(ContentNegotiation) { json(networkJson) }
    // A 4xx or 5xx answer throws ResponseException, which networkCall turns into HttpError.
    expectSuccess = true
}
