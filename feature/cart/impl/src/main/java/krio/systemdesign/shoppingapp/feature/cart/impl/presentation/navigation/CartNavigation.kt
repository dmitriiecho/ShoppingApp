package krio.systemdesign.shoppingapp.feature.cart.impl.presentation.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import kotlinx.serialization.json.Json
import krio.systemdesign.shoppingapp.core.composeutils.animation.LocalNavAnimatedVisibilityScope
import krio.systemdesign.shoppingapp.core.config.DeepLinkConfig
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart.CartEvent
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart.CartScreen
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart.CartViewModel
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode

class CartNavigationScope(val builder: NavGraphBuilder)

val NavGraphBuilder.cart: CartNavigationScope
    get() = CartNavigationScope(this)

// onClose exits the whole cart feature. The cart can be the root of a tab, where there is nothing to close,
// or it can be embedded in a flow the user returns back from.
fun CartNavigationScope.graph(
    navController: NavController,
    onClose: () -> Unit,
    onOpenCheckout: () -> Unit,
    onOpenPromo: (resultKey: String) -> Unit,
    onOpenProduct: (productId: String, productName: String, imageUrl: String) -> Unit,
) {
    builder.navigation<CartRoutes.Graph>(
        startDestination = CartRoutes.Cart,
    ) {
        composable<CartRoutes.Cart>(
            deepLinks = listOf(navDeepLink<CartRoutes.Cart>(basePath = CART_DEEP_LINK)),
        ) { entry ->
            val viewModel: CartViewModel = hiltViewModel()

            val promoResult by entry.savedStateHandle
                .getStateFlow<String?>(CartResults.PROMO_RESULT_KEY, null)
                .collectAsStateWithLifecycle()

            LaunchedEffect(promoResult) {
                promoResult?.let { result ->
                    viewModel.onEvent(CartEvent.OnPromoCodeApplied(Json.decodeFromString<PromoCode>(result)))
                    entry.savedStateHandle.remove<String>(CartResults.PROMO_RESULT_KEY)
                }
            }

            // Product images fly between the cart and the product details screen.
            CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                CartScreen(
                    viewModel = viewModel,
                    onBack = onClose,
                    onOpenCheckout = onOpenCheckout,
                    onOpenPromo = {
                        onOpenPromo(CartResults.PROMO_RESULT_KEY)
                    },
                    onOpenProduct = onOpenProduct,
                )
            }
        }
    }
}

private const val CART_DEEP_LINK = "${DeepLinkConfig.BASE_URL}/cart"
