package krio.systemdesign.shoppingapp.navigation

import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavGraph
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavUri
import co.touchlab.kermit.Logger
import krio.systemdesign.shoppingapp.core.config.DeepLinkConfig
import krio.systemdesign.shoppingapp.navigation.bottombar.BottomNavRoutes
import krio.systemdesign.shoppingapp.navigation.bottombar.navigateToBottomTab

// Opens a link the way the user would: Back from /cart leaves the app, from /product/{id} returns to the catalog.
internal fun NavHostController.openDeepLink(link: String) {
    val request = NavDeepLinkRequest.Builder.fromUri(NavUri(normalizeDeepLink(link))).build()

    // A link no tab can open (e.g. /catalog/shoes) is ignored.
    val tab = BottomNavRoutes.all.firstOrNull { tabGraph(it).hasDeepLink(request) }
    if (tab == null) {
        Logger.w { "No screen opens deep link $link" }
        return
    }
    val tabRoot = tabGraph(tab).findStartDestination()

    // As a tap in the bottom bar.
    navigateToBottomTab(tab)
    // The tab roots have no arguments, so the route is the exact one.
    popBackStack(checkNotNull(tabRoot.route), inclusive = false)
    // E.g. a product link opens its screen over the tab root.
    if (!tabRoot.hasDeepLink(request)) {
        navigate(request)
    }
}

// Brings a link to the form of the screens' link patterns. Those are https only, so a link with the iOS app's
// scheme becomes https: shoppingapp://cart is DeepLinkConfig.BASE_URL/cart. They have no trailing slash, but /cart/
// and /product/1/ must open too: the slash goes from the end of the path, before a query or a fragment.
internal fun normalizeDeepLink(link: String): String {
    val appSchemePrefix = "${DeepLinkConfig.APP_SCHEME}://"
    val httpsLink = if (link.startsWith(appSchemePrefix, ignoreCase = true)) {
        "${DeepLinkConfig.BASE_URL}/${link.substring(appSchemePrefix.length)}"
    } else {
        link
    }
    val pathEnd = httpsLink.indexOfAny(charArrayOf('?', '#')).takeIf { it >= 0 } ?: httpsLink.length
    return httpsLink.substring(0, pathEnd).trimEnd('/') + httpsLink.substring(pathEnd)
}

private fun NavHostController.tabGraph(tab: Any): NavGraph = graph.findNode(tab) as NavGraph
