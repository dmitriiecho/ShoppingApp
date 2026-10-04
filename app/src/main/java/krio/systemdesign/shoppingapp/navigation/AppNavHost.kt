package krio.systemdesign.shoppingapp.navigation

import android.net.Uri
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import krio.systemdesign.shoppingapp.core.ui.animation.LocalSharedTransitionScope
import krio.systemdesign.shoppingapp.feature.cart.presentation.navigation.CartRoutes
import krio.systemdesign.shoppingapp.feature.cart.presentation.navigation.cart
import krio.systemdesign.shoppingapp.feature.cart.presentation.navigation.graph as cartGraph
import krio.systemdesign.shoppingapp.feature.cart.presentation.navigation.toCartPromoResult
import krio.systemdesign.shoppingapp.feature.catalog.presentation.navigation.CatalogRoutes
import krio.systemdesign.shoppingapp.feature.catalog.presentation.navigation.catalog
import krio.systemdesign.shoppingapp.feature.catalog.presentation.navigation.graph as catalogGraph
import krio.systemdesign.shoppingapp.feature.catalog.presentation.navigation.productDetailsScreen
import krio.systemdesign.shoppingapp.feature.checkout.presentation.navigation.CheckoutRoutes
import krio.systemdesign.shoppingapp.feature.checkout.presentation.navigation.checkout
import krio.systemdesign.shoppingapp.feature.checkout.presentation.navigation.graph as checkoutGraph
import krio.systemdesign.shoppingapp.feature.promo.presentation.navigation.PromoRoutes
import krio.systemdesign.shoppingapp.feature.promo.presentation.navigation.graph as promoGraph
import krio.systemdesign.shoppingapp.feature.promo.presentation.navigation.promo
import krio.systemdesign.shoppingapp.feature.settings.presentation.navigation.SettingsRoutes
import krio.systemdesign.shoppingapp.feature.settings.presentation.navigation.graph as settingsGraph
import krio.systemdesign.shoppingapp.feature.settings.presentation.navigation.settings
import krio.systemdesign.shoppingapp.navigation.bottombar.AppBottomBar
import krio.systemdesign.shoppingapp.navigation.bottombar.BottomNavRoutes
import krio.systemdesign.shoppingapp.navigation.bottombar.navigateToBottomTab
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Composable
fun AppNavHost(
    deepLinks: Flow<Uri>,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val isSwitchingTabs by navController.isSwitchingTabsAsState()

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = { AppBottomBar(navController) },
    ) { innerPadding ->
        // The layer shared elements fly over, e.g. a product image from the list to the product screen.
        SharedTransitionLayout(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        ) {
            // Only within a tab: otherwise a product that is both in the catalog and the cart would fly between tabs.
            CompositionLocalProvider(LocalSharedTransitionScope provides this.takeUnless { isSwitchingTabs }) {
                NavHost(
                    navController = navController,
                    startDestination = BottomNavRoutes.CatalogTab,
                    modifier = Modifier
                        // The leaving screen still takes taps: a double tap on Back closed two screens.
                        .blockTouchesDuringTransitions(navController),
                    // No transition: NavHost's default fade made the app feel slow. It stays only where
                    // the product image flies between screens.
                    enterTransition = {
                        if (isProductImageTransition()) fadeIn(tween(PRODUCT_IMAGE_TRANSITION_MS)) else EnterTransition.None
                    },
                    exitTransition = {
                        if (isProductImageTransition()) fadeOut(tween(PRODUCT_IMAGE_TRANSITION_MS)) else ExitTransition.None
                    },
                ) {
                    navigation<BottomNavRoutes.CatalogTab>(
                        startDestination = CatalogRoutes.Graph,
                    ) {
                        catalog.catalogGraph(
                            navController = navController,
                            onClose = {
                                // Tab root: nothing to close.
                            },
                        )
                    }

                    navigation<BottomNavRoutes.CartTab>(
                        startDestination = CartRoutes.Graph,
                    ) {
                        cart.cartGraph(
                            navController = navController,
                            onClose = {
                                // Tab root: nothing to close.
                            },
                            onOpenCheckout = {
                                navController.navigate(CheckoutRoutes.Graph)
                            },
                            onOpenPromo = { resultKey ->
                                navController.navigate(PromoRoutes.Graph(resultKey = resultKey))
                            },
                            onOpenProduct = { productId, productName, imageUrl ->
                                navController.navigate(
                                    CartProductRoute(
                                        productId = productId,
                                        productName = productName,
                                        imageUrl = imageUrl,
                                    ),
                                )
                            },
                        )

                        promo.promoGraph(
                            navController = navController,
                            onClose = {
                                navController.popBackStack<PromoRoutes.Graph>(inclusive = true)
                            },
                            onCloseWithResult = { resultKey, promoCode ->
                                navController.popBackStack<PromoRoutes.Graph>(inclusive = true)
                                navController.currentBackStackEntry
                                    ?.savedStateHandle
                                    ?.set(resultKey, promoCode.toCartPromoResult())
                            },
                        )

                        catalog.productDetailsScreen<CartProductRoute>(
                            onBack = { navController.popBackStack() },
                        )
                    }

                    navigation<BottomNavRoutes.SettingsTab>(
                        startDestination = SettingsRoutes.Graph,
                    ) {
                        settings.settingsGraph(
                            navController = navController,
                            onClose = {
                                // Tab root: nothing to close.
                            },
                        )
                    }

                    checkout.checkoutGraph(
                        navController = navController,
                        onClose = {
                            navController.popBackStack<CheckoutRoutes.Graph>(inclusive = true)
                        },
                    )
                }
            }
        }

        // After NavHost: it sets the graph the links are resolved against.
        LaunchedEffect(navController, deepLinks) {
            deepLinks.collect { navController.openDeepLink(it) }
        }
    }
}

// Opens a link the way the user would: Back from /cart leaves the app, from /product/{id} returns to the catalog.
private fun NavHostController.openDeepLink(uri: Uri) {
    // Link patterns have no trailing slash, but /cart/ and /product/1/ must open too.
    val path = uri.encodedPath?.trimEnd('/')
    val link = NavDeepLinkRequest.Builder.fromUri(uri.buildUpon().encodedPath(path).build()).build()

    // A link no tab can open (e.g. /catalog/shoes) is ignored.
    val tab = listOf(BottomNavRoutes.CatalogTab, BottomNavRoutes.CartTab)
        .firstOrNull { tabGraph(it).hasDeepLink(link) }
        ?: return
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

// Where the product image flies: within the catalog, and between the cart and a product opened from it.
private fun AnimatedContentTransitionScope<NavBackStackEntry>.isProductImageTransition(): Boolean {
    val screens = listOf(initialState.destination, targetState.destination)
    return screens.all { it.isIn<CatalogRoutes.Graph>() } ||
        (screens.any { it.isIn<CartRoutes.Graph>() } && screens.any { it.hasRoute<CartProductRoute>() })
}

private inline fun <reified T : Any> NavDestination.isIn(): Boolean = hierarchy.any { it.hasRoute<T>() }

// NavHost's default.
private const val PRODUCT_IMAGE_TRANSITION_MS = 700

// During a transition both screens are visible; from different tabs means the tab is switching.
@Composable
private fun NavHostController.isSwitchingTabsAsState(): State<Boolean> =
    remember(this) {
        visibleEntries.map { entries ->
            entries.mapNotNull { it.destination.bottomTab()?.id }.distinct().size > 1
        }
    }.collectAsState(initial = false)

// The tab graph the screen is in; null outside tabs (checkout).
private fun NavDestination.bottomTab(): NavDestination? =
    hierarchy.firstOrNull {
        it.hasRoute<BottomNavRoutes.CatalogTab>() ||
            it.hasRoute<BottomNavRoutes.CartTab>() ||
            it.hasRoute<BottomNavRoutes.SettingsTab>()
    }

// The top screen is RESUMED only once its transition ends. Checked on each touch, not on recomposition:
// a second tap can come first.
private fun Modifier.blockTouchesDuringTransitions(navController: NavHostController): Modifier =
    pointerInput(navController) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                val topScreenState = navController.currentBackStackEntry?.lifecycle?.currentState
                if (topScreenState != Lifecycle.State.RESUMED) {
                    event.changes.forEach { it.consume() }
                }
            }
        }
    }
