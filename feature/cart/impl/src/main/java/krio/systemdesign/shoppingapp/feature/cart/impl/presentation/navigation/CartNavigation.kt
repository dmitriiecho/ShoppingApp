package krio.systemdesign.shoppingapp.feature.cart.impl.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import androidx.navigation3.runtime.result.LocalResultEventBus
import androidx.navigation3.runtime.result.ResultEffect
import krio.systemdesign.shoppingapp.core.composeutils.animation.sharedElementScreen
import krio.systemdesign.shoppingapp.core.composeutils.navigation.Navigator
import krio.systemdesign.shoppingapp.core.composeutils.viewmodel.injectedViewModel
import krio.systemdesign.shoppingapp.core.config.DeepLinkConfig
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart.CartEvent
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart.CartScreen
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart.CartViewModel
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode

class CartNavigationScope internal constructor(internal val scope: EntryProviderScope<NavKey>)

val EntryProviderScope<NavKey>.cart: CartNavigationScope
    get() = CartNavigationScope(this)

// onClose exits the whole cart feature. The cart can be the root of a tab, where there is nothing to close,
// or it can be embedded in a flow the user returns back from.
fun CartNavigationScope.entries(
    navigator: Navigator,
    onClose: () -> Unit,
    onOpenCheckout: () -> Unit,
    onOpenPromo: (resultKey: String) -> Unit,
    onOpenProduct: (productId: String, productName: String, imageUrl: String) -> Unit,
) {
    // Product images fly between the cart and the product details screen.
    scope.entry<CartRoutes.Cart>(metadata = sharedElementScreen()) {
        val viewModel: CartViewModel = injectedViewModel()

        // The code applied on the promo code screen. Removed once handled, so it isn't applied again when
        // the cart comes back into composition.
        val resultEventBus = LocalResultEventBus.current
        ResultEffect<PromoCode>(CartResults.PROMO_RESULT_KEY) { promoCode ->
            viewModel.onEvent(CartEvent.OnPromoCodeApplied(promoCode))
            resultEventBus.removeResult(CartResults.PROMO_RESULT_KEY)
        }

        CartScreen(
            viewModel = viewModel,
            onBack = onClose,
            onOpenCheckout = onOpenCheckout,
            onOpenPromo = { onOpenPromo(CartResults.PROMO_RESULT_KEY) },
            onOpenProduct = onOpenProduct,
        )
    }
}

// The cart's link: it opens the cart tab.
val cartDeepLinks: List<UriDeepLinkMatcher<NavKey>> = listOf(
    UriDeepLinkMatcher(DeepLinkUri(CART_DEEP_LINK), CartRoutes.Cart.serializer()),
)

private const val CART_DEEP_LINK = "${DeepLinkConfig.BASE_URL}/cart"
