package krio.systemdesign.shoppingapp.core.config

object DeepLinkConfig {
    // Домен задаётся в gradle.properties (deepLinkHost), оттуда же он попадает в манифест.
    const val BASE_URI = "https://${BuildConfig.DEEP_LINK_HOST}"

    // Страница со ссылками для ручной проверки: docs/deeplinks.html, её публикует GitHub Pages этого репозитория.
    const val TEST_PAGE_URI = "$BASE_URI/ShoppingApp/deeplinks.html"
}
