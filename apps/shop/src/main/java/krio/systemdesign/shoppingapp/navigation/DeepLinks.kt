package krio.systemdesign.shoppingapp.navigation

import android.net.Uri
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavGraph
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import krio.systemdesign.shoppingapp.navigation.bottombar.BottomNavRoutes
import krio.systemdesign.shoppingapp.navigation.bottombar.navigateToBottomTab
import timber.log.Timber

// Opens a link the way the user would: Back from /cart leaves the app, from /product/{id} returns to the catalog.
internal fun NavHostController.openDeepLink(uri: Uri) {
    // Link patterns have no trailing slash, but /cart/ and /product/1/ must open too.
    val path = uri.encodedPath?.trimEnd('/')
    val link = NavDeepLinkRequest.Builder.fromUri(uri.buildUpon().encodedPath(path).build()).build()

    // A link no tab can open (e.g. /catalog/shoes) is ignored.
    val tab = BottomNavRoutes.all.firstOrNull { tabGraph(it).hasDeepLink(link) }
    if (tab == null) {
        Timber.w("No screen opens deep link %s", uri)
        return
    }
    val tabRoot = tabGraph(tab).findStartDestination()

    // As a tap in the bottom bar.
    navigateToBottomTab(tab)
    popBackStack(tabRoot.id, inclusive = false)
    // E.g. a product link opens its screen over the tab root.
    if (!tabRoot.hasDeepLink(link)) {
        navigate(link)
    }
}

private fun NavHostController.tabGraph(tab: Any): NavGraph = graph.findNode(tab) as NavGraph
