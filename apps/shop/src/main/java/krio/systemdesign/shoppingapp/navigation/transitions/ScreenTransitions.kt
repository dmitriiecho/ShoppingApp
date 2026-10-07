package krio.systemdesign.shoppingapp.navigation.transitions

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.navigation.CartRoutes
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.navigation.CatalogRoutes
import krio.systemdesign.shoppingapp.navigation.CartProductRoute
import krio.systemdesign.shoppingapp.shared.ui.product.PRODUCT_IMAGE_TRANSITION_MILLIS

// No transition: a fade made the app feel slow. Screens fade only where the product image flies,
// in step with it (PRODUCT_IMAGE_TRANSITION_MILLIS).
internal fun AnimatedContentTransitionScope<NavBackStackEntry>.appEnterTransition(): EnterTransition =
    if (isProductImageTransition()) fadeIn(tween(PRODUCT_IMAGE_TRANSITION_MILLIS)) else EnterTransition.None

internal fun AnimatedContentTransitionScope<NavBackStackEntry>.appExitTransition(): ExitTransition =
    if (isProductImageTransition()) fadeOut(tween(PRODUCT_IMAGE_TRANSITION_MILLIS)) else ExitTransition.None

// Where the product image flies: within the catalog, and between the cart and a product opened from it.
private fun AnimatedContentTransitionScope<NavBackStackEntry>.isProductImageTransition(): Boolean {
    val from = initialState.destination
    val to = targetState.destination
    val withinCatalog = from.isIn<CatalogRoutes.Graph>() && to.isIn<CatalogRoutes.Graph>()
    val cartToProduct = from.isIn<CartRoutes.Graph>() && to.hasRoute<CartProductRoute>()
    val productToCart = from.hasRoute<CartProductRoute>() && to.isIn<CartRoutes.Graph>()
    return withinCatalog || cartToProduct || productToCart
}

private inline fun <reified T : Any> NavDestination.isIn(): Boolean = hierarchy.any { it.hasRoute<T>() }
