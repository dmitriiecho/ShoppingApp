package krio.systemdesign.shoppingapp.core.config

object DeepLinkConfig {
    // The same domain is specified in the intent-filter in app/src/main/AndroidManifest.xml.
    // Update both places if it changes.
    const val BASE_URL = "https://dmitriiecho.github.io"

    // Page with links for manual testing: docs/deeplinks.html.
    const val TEST_PAGE_URL = "$BASE_URL/ShoppingApp/deeplinks.html"
}
