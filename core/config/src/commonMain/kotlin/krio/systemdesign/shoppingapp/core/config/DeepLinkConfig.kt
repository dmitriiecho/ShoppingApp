package krio.systemdesign.shoppingapp.core.config

object DeepLinkConfig {
    // The same domain and path prefix are specified in the intent-filter in apps/shop/android/src/main/AndroidManifest.xml.
    // Update both places if they change. The prefix keeps the app's links apart from other projects on the domain;
    // it is also where GitHub Pages publishes docs/.
    const val BASE_URL = "https://dmitriiecho.github.io/ShoppingApp"

    // The iOS app's own scheme, registered in CFBundleURLTypes in apps/shop/ios/iosApp/Info.plist: iOS opens https
    // links in an app only for a signed team with a file on the site. shoppingapp://cart is BASE_URL/cart.
    const val APP_SCHEME = "shoppingapp"

    // Pages with links for manual testing, one per platform and language: docs/deeplinks/android/, docs/deeplinks/ios/.
    val TEST_PAGES_URL = "$BASE_URL/deeplinks/$testPagesFolder"
}

// The platform's folder of the test pages: each platform's page has only the links its app opens.
internal expect val testPagesFolder: String
