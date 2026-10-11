package krio.systemdesign.shoppingapp.core.network.di

import io.ktor.client.engine.HttpClientEngineFactory

// The platform's HTTP engine: OkHttp on Android, NSURLSession (Darwin) on iOS.
internal expect val httpClientEngine: HttpClientEngineFactory<*>
