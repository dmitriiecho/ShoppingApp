package krio.systemdesign.shoppingapp.feature.promo.presentation.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import krio.systemdesign.shoppingapp.feature.promo.presentation.promocode.PromoCodeScreen

class PromoNavigationScope(val builder: NavGraphBuilder)

val NavGraphBuilder.promo: PromoNavigationScope
    get() = PromoNavigationScope(this)

fun PromoNavigationScope.graph(
    navController: NavController,
    onClose: () -> Unit,
    onCloseWithResult: (resultKey: String, result: PromoCodeResult) -> Unit,
) {
    builder.navigation<PromoRoutes.Graph>(
        startDestination = PromoRoutes.PromoCode,
    ) {
        composable<PromoRoutes.PromoCode> { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry<PromoRoutes.Graph>()
            }
            val resultKey = parentEntry.toRoute<PromoRoutes.Graph>().resultKey

            PromoCodeScreen(
                onBack = onClose,
                onCloseWithResult = { result ->
                    onCloseWithResult(resultKey, result)
                },
            )
        }
    }
}
