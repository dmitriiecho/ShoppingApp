package krio.systemdesign.shoppingapp.server

import kotlinx.serialization.json.Json

// How the server reads requests and writes responses. Public so that tests check the JSON the app really gets.
// Same settings as the app's networkJson (NetworkJson.kt in :core:network).
val serverJson: Json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    encodeDefaults = true
}
