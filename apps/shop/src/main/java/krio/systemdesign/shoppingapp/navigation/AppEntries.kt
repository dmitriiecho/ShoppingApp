package krio.systemdesign.shoppingapp.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.result.ResultEventBus
import kotlinx.serialization.Serializable
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.navigation.cart
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.navigation.entries as cartEntries
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.navigation.ProductDetailsRoute
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.navigation.catalog
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.navigation.entries as catalogEntries
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.navigation.productDetailsEntry
import krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.navigation.CheckoutRoutes
import krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.navigation.checkout
import krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.navigation.entries as checkoutEntries
import krio.systemdesign.shoppingapp.feature.promo.impl.presentation.navigation.PromoRoutes
import krio.systemdesign.shoppingapp.feature.promo.impl.presentation.navigation.entries as promoEntries
import krio.systemdesign.shoppingapp.feature.promo.impl.presentation.navigation.promo
import krio.systemdesign.shoppingapp.feature.settings.impl.presentation.navigation.entries as settingsEntries
import krio.systemdesign.shoppingapp.feature.settings.impl.presentation.navigation.settings

// The app's screens: three tabs (BottomTab), plus checkout that opens over them. Every path from one feature
// to another is a callback here.
internal fun EntryProviderScope<NavKey>.appEntries(
    navigator: AppNavigator,
    resultEventBus: ResultEventBus,
) {
    catalog.catalogEntries(
        navigator = navigator,
        onClose = {}, // Tab root: nothing to close.
    )
    cart.cartEntries(
        navigator = navigator,
        onClose = {}, // Tab root: nothing to close.
        onOpenCheckout = { navigator.openOverTabs(CheckoutRoutes.Checkout) },
        onOpenPromo = { resultKey -> navigator.navigate(PromoRoutes.PromoCode(resultKey)) },
        onOpenProduct = { productId, productName, imageUrl ->
            navigator.navigate(CartProductRoute(productId, productName, imageUrl))
        },
    )
    promo.promoEntries(
        navigator = navigator,
        onClose = { navigator.popUpTo(PromoRoutes.PromoCode::class) },
        onCloseWithResult = { resultKey, promoCode ->
            resultEventBus.sendResult(resultKey, promoCode)
            navigator.popUpTo(PromoRoutes.PromoCode::class)
        },
    )
    catalog.productDetailsEntry<CartProductRoute>(onBack = navigator::goBack)
    settings.settingsEntries(
        navigator = navigator,
        onClose = {}, // Tab root: nothing to close.
    )
    checkout.checkoutEntries(
        navigator = navigator,
        onClose = navigator::closeOverTabs,
    )
}

// Product details opened from the cart: stays in the cart tab.
@Serializable
data class CartProductRoute(
    override val productId: String,
    override val productName: String? = null,
    override val imageUrl: String? = null,
) : ProductDetailsRoute,
    NavKey
