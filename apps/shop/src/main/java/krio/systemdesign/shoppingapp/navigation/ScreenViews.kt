package krio.systemdesign.shoppingapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.navigation.cartAnalyticsScreen
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.navigation.catalogAnalyticsScreen
import krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.navigation.checkoutAnalyticsScreen
import krio.systemdesign.shoppingapp.feature.promo.impl.presentation.navigation.promoAnalyticsScreen
import krio.systemdesign.shoppingapp.feature.settings.impl.presentation.navigation.settingsAnalyticsScreen
import krio.systemdesign.shoppingapp.shared.analytics.event.AnalyticsScreen

// Reports the screen the user sees: after each navigation and each time the app comes back to the foreground,
// since the current back stack entry is replayed when collection restarts. Screens without a name aren't reported.
@Composable
internal fun ObserveScreenViews(
    navController: NavController,
    onScreenView: (AnalyticsScreen) -> Unit,
) {
    val currentOnScreenView by rememberUpdatedState(onScreenView)
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(navController, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            navController.currentBackStackEntryFlow.collect { entry ->
                entry.destination.analyticsScreen()?.let(currentOnScreenView)
            }
        }
    }
}

private fun NavDestination.analyticsScreen(): AnalyticsScreen? = catalogAnalyticsScreen()
    ?: cartAnalyticsScreen()
    ?: promoAnalyticsScreen()
    ?: checkoutAnalyticsScreen()
    ?: settingsAnalyticsScreen()
    ?: AnalyticsScreen.ProductDetails.takeIf { hasRoute<CartProductRoute>() }
