package krio.systemdesign.shoppingapp.core.config

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class NetworkSettings(
    val baseUrl: String,
    val connectTimeout: Duration = 30.seconds,
    val readTimeout: Duration = 30.seconds,
    val writeTimeout: Duration = 30.seconds,
)
