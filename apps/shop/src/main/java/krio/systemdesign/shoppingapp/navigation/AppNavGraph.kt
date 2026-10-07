package krio.systemdesign.shoppingapp.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigation
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.navigation.CartRoutes
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.navigation.cart
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.navigation.graph as cartGraph
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.navigation.CatalogRoutes
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.navigation.ProductDetailsRoute
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.navigation.catalog
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.navigation.graph as catalogGraph
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.navigation.productDetailsScreen
import krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.navigation.CheckoutRoutes
import krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.navigation.checkout
import krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.navigation.graph as checkoutGraph
import krio.systemdesign.shoppingapp.feature.promo.impl.presentation.navigation.PromoRoutes
import krio.systemdesign.shoppingapp.feature.promo.impl.presentation.navigation.graph as promoGraph
import krio.systemdesign.shoppingapp.feature.promo.impl.presentation.navigation.promo
import krio.systemdesign.shoppingapp.feature.settings.impl.presentation.navigation.SettingsRoutes
import krio.systemdesign.shoppingapp.feature.settings.impl.presentation.navigation.graph as settingsGraph
import krio.systemdesign.shoppingapp.feature.settings.impl.presentation.navigation.settings
import krio.systemdesign.shoppingapp.navigation.bottombar.BottomNavRoutes

// The app's screens: three tabs, plus checkout that opens over them.
internal fun NavGraphBuilder.appGraph(navController: NavHostController) {
    catalogTab(navController)
    cartTab(navController)
    settingsTab(navController)
    checkout.checkoutGraph(
        navController = navController,
        onClose = { navController.popBackStack<CheckoutRoutes.Graph>(inclusive = true) },
    )
}

private fun NavGraphBuilder.catalogTab(navController: NavHostController) {
    navigation<BottomNavRoutes.CatalogTab>(startDestination = CatalogRoutes.Graph) {
        catalog.catalogGraph(
            navController = navController,
            onClose = {}, // Tab root: nothing to close.
        )
    }
}

private fun NavGraphBuilder.cartTab(navController: NavHostController) {
    navigation<BottomNavRoutes.CartTab>(startDestination = CartRoutes.Graph) {
        cart.cartGraph(
            navController = navController,
            onClose = {}, // Tab root: nothing to close.
            onOpenCheckout = { navController.navigate(CheckoutRoutes.Graph) },
            onOpenPromo = { resultKey -> navController.navigate(PromoRoutes.Graph(resultKey = resultKey)) },
            onOpenProduct = { productId, productName, imageUrl ->
                navController.navigate(CartProductRoute(productId, productName, imageUrl))
            },
        )
        promo.promoGraph(
            navController = navController,
            onClose = { navController.popBackStack<PromoRoutes.Graph>(inclusive = true) },
            onCloseWithResult = { resultKey, promoCode ->
                navController.popBackStack<PromoRoutes.Graph>(inclusive = true)
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set(resultKey, Json.encodeToString(promoCode))
            },
        )
        catalog.productDetailsScreen<CartProductRoute>(
            onBack = { navController.popBackStack() },
        )
    }
}

private fun NavGraphBuilder.settingsTab(navController: NavHostController) {
    navigation<BottomNavRoutes.SettingsTab>(startDestination = SettingsRoutes.Graph) {
        settings.settingsGraph(
            navController = navController,
            onClose = {}, // Tab root: nothing to close.
        )
    }
}

// Product details opened from the cart: stays in the cart tab.
@Serializable
data class CartProductRoute(
    override val productId: String,
    override val productName: String? = null,
    override val imageUrl: String? = null,
) : ProductDetailsRoute
