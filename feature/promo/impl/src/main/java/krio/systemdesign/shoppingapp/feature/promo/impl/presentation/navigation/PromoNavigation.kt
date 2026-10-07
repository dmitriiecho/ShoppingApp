package krio.systemdesign.shoppingapp.feature.promo.impl.presentation.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import krio.systemdesign.shoppingapp.feature.promo.impl.presentation.promocode.PromoCodeScreen
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode

class PromoNavigationScope(val builder: NavGraphBuilder)

val NavGraphBuilder.promo: PromoNavigationScope
    get() = PromoNavigationScope(this)

// resultKey works like a request code: the caller passes it and gets the applied code back under it,
// so one promo graph can serve several callers. onClose leaves without a result.
fun PromoNavigationScope.graph(
    navController: NavController,
    onClose: () -> Unit,
    onCloseWithResult: (resultKey: String, promoCode: PromoCode) -> Unit,
) {
    builder.navigation<PromoRoutes.Graph>(
        startDestination = PromoRoutes.PromoCode,
    ) {
        composable<PromoRoutes.PromoCode> { entry ->
            // resultKey is an argument of the graph route, not of this screen's route.
            val parentEntry = remember(entry) {
                navController.getBackStackEntry<PromoRoutes.Graph>()
            }
            val resultKey = parentEntry.toRoute<PromoRoutes.Graph>().resultKey

            PromoCodeScreen(
                onBack = onClose,
                onCloseWithResult = { promoCode ->
                    onCloseWithResult(resultKey, promoCode)
                },
            )
        }
    }
}
