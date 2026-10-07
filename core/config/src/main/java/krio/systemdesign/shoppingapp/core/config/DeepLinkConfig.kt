package krio.systemdesign.shoppingapp.core.config

object DeepLinkConfig {
    // The same domain and path prefix are specified in the intent-filter in apps/shop/src/main/AndroidManifest.xml.
    // Update both places if they change. The prefix keeps the app's links apart from other projects on the domain;
    // it is also where GitHub Pages publishes docs/.
    const val BASE_URL = "https://dmitriiecho.github.io/ShoppingApp"

    // Pages with links for manual testing, one per language: docs/deeplinks/.
    const val TEST_PAGES_URL = "$BASE_URL/deeplinks"
}
