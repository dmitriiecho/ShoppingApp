package krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import krio.systemdesign.shoppingapp.core.composeutils.navigation.Navigator
import krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.checkout.CheckoutScreen

class CheckoutNavigationScope internal constructor(internal val scope: EntryProviderScope<NavKey>)

val EntryProviderScope<NavKey>.checkout: CheckoutNavigationScope
    get() = CheckoutNavigationScope(this)

fun CheckoutNavigationScope.entries(
    navigator: Navigator,
    onClose: () -> Unit,
) {
    scope.entry<CheckoutRoutes.Checkout> {
        CheckoutScreen(onClose = onClose)
    }
}
