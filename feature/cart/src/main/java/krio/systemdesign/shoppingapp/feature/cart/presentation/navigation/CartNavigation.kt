package krio.systemdesign.shoppingapp.feature.cart.presentation.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import krio.systemdesign.shoppingapp.core.config.DeepLinkConfig
import krio.systemdesign.shoppingapp.feature.cart.presentation.cart.CartScreen
import krio.systemdesign.shoppingapp.feature.cart.presentation.cart.CartViewModel

class CartNavigationScope(val builder: NavGraphBuilder)

val NavGraphBuilder.cart: CartNavigationScope
    get() = CartNavigationScope(this)

// onClose — выход из фичи корзины целиком. Корзина может быть корнем вкладки, где закрывать некуда,
// а может быть встроена во флоу, из которого пользователь возвращается назад.
fun CartNavigationScope.graph(
    navController: NavController,
    onClose: () -> Unit,
    onOpenCheckout: () -> Unit,
    onOpenPromo: (resultKey: String) -> Unit,
    onOpenProduct: (productId: String, productName: String) -> Unit,
) {
    builder.navigation<CartRoutes.Graph>(
        startDestination = CartRoutes.Cart,
    ) {
        composable<CartRoutes.Cart>(
            deepLinks = listOf(
                navDeepLink<CartRoutes.Cart>(
                    basePath = "${DeepLinkConfig.BASE_URI}/cart",
                ),
            ),
        ) { entry ->
            val viewModel: CartViewModel = hiltViewModel()

            val promoResult by entry.savedStateHandle
                .getStateFlow<CartPromoResult?>(CartResults.PROMO_RESULT_KEY, null)
                .collectAsStateWithLifecycle()

            LaunchedEffect(promoResult) {
                promoResult?.let { result ->
                    viewModel.onPromoApplied(result.toPromoCode())
                    entry.savedStateHandle.remove<CartPromoResult>(CartResults.PROMO_RESULT_KEY)
                }
            }

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
