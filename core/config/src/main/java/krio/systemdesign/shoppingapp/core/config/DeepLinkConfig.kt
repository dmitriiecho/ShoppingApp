package krio.systemdesign.shoppingapp.core.config

object DeepLinkConfig {
    // Домен задаётся в gradle.properties (deepLinkHost), оттуда же он попадает в манифест.
    const val BASE_URI = "https://${BuildConfig.DEEP_LINK_HOST}"
}
