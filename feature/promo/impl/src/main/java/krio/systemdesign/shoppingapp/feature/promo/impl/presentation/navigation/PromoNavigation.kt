package krio.systemdesign.shoppingapp.feature.promo.impl.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import krio.systemdesign.shoppingapp.core.composeutils.navigation.Navigator
import krio.systemdesign.shoppingapp.feature.promo.impl.presentation.promocode.PromoCodeScreen
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode

class PromoNavigationScope internal constructor(internal val scope: EntryProviderScope<NavKey>)

val EntryProviderScope<NavKey>.promo: PromoNavigationScope
    get() = PromoNavigationScope(this)

// onClose leaves without a result; onCloseWithResult returns the applied code under the caller's resultKey,
// so one promo feature can serve several callers.
fun PromoNavigationScope.entries(
    navigator: Navigator,
    onClose: () -> Unit,
    onCloseWithResult: (resultKey: String, promoCode: PromoCode) -> Unit,
) {
    scope.entry<PromoRoutes.PromoCode> { key ->
        PromoCodeScreen(
            onBack = onClose,
            onCloseWithResult = { promoCode -> onCloseWithResult(key.resultKey, promoCode) },
        )
    }
}
