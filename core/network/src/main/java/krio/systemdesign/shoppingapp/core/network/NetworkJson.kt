package krio.systemdesign.shoppingapp.core.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Converter
import retrofit2.converter.kotlinx.serialization.asConverterFactory

// How the app reads and writes the server's JSON. Public so that tests read it the same way (networkTest).
// The server has the same settings (serverJson in server/.../ServerJson.kt).
val networkJson: Json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    encodeDefaults = true
}

// Retrofit's converter for networkJson.
val networkJsonConverterFactory: Converter.Factory =
    networkJson.asConverterFactory("application/json; charset=UTF-8".toMediaType())
