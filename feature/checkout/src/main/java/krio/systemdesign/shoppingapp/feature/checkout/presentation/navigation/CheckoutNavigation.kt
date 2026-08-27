package krio.systemdesign.shoppingapp.feature.checkout.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import krio.systemdesign.shoppingapp.feature.checkout.presentation.checkout.CheckoutScreen

class CheckoutNavigationScope(val builder: NavGraphBuilder)

val NavGraphBuilder.checkout: CheckoutNavigationScope
    get() = CheckoutNavigationScope(this)

fun CheckoutNavigationScope.graph(
    navController: NavController,
    onClose: () -> Unit,
) {
    builder.navigation<CheckoutRoutes.Graph>(
        startDestination = CheckoutRoutes.Checkout,
    ) {
        composable<CheckoutRoutes.Checkout> {
            CheckoutScreen(onClose = onClose)
        }
    }
}
