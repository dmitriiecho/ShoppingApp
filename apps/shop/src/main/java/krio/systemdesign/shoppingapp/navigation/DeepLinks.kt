package krio.systemdesign.shoppingapp.navigation

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import co.touchlab.kermit.Logger
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.navigation.cartDeepLinks
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.navigation.catalogDeepLinks
import krio.systemdesign.shoppingapp.navigation.bottombar.BottomTab

// Opens a link the way the user would: Back from /cart leaves the app, from /product/{id} returns to the catalog.
internal fun AppNavigator.openDeepLink(uri: Uri) {
    // Link patterns have no trailing slash, but /cart/ and /product/1/ must open too.
    val path = uri.encodedPath?.trimEnd('/')
    val request = DeepLinkRequest(DeepLinkUri(uri.buildUpon().encodedPath(path).build().toString()))

    // A link no tab can open (e.g. /catalog/shoes) is ignored.
    val match = tabDeepLinks
        .mapNotNull { (tab, matcher) -> matcher.match(request)?.let { result -> tab to result } }
        .maxWithOrNull { first, second -> first.second.compareTo(second.second) }
    if (match == null) {
        Logger.w { "No screen opens deep link $uri" }
        return
    }
    val (tab, result) = match
    openDeepLink(tab, result.key)
}

// The tab each feature's links open in.
private val tabDeepLinks: List<Pair<BottomTab, UriDeepLinkMatcher<NavKey>>> =
    catalogDeepLinks.map { BottomTab.Catalog to it } + cartDeepLinks.map { BottomTab.Cart to it }
