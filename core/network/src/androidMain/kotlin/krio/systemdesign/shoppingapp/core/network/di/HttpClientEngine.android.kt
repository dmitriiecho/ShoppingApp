package krio.systemdesign.shoppingapp.core.network.di

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

internal actual val httpClientEngine: HttpClientEngineFactory<*> = OkHttp
