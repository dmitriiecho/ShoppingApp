package krio.systemdesign.shoppingapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation3.runtime.NavKey
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.navigation.cartAnalyticsScreen
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.navigation.catalogAnalyticsScreen
import krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.navigation.checkoutAnalyticsScreen
import krio.systemdesign.shoppingapp.feature.promo.impl.presentation.navigation.promoAnalyticsScreen
import krio.systemdesign.shoppingapp.feature.settings.impl.presentation.navigation.settingsAnalyticsScreen
import krio.systemdesign.shoppingapp.shared.analytics.event.AnalyticsScreen

// Reports the screen the user sees: after each navigation and each time the app comes back to the foreground,
// since snapshotFlow starts with the current screen when collection restarts. Screens without a name aren't reported.
@Composable
internal fun ObserveScreenViews(
    state: NavigationState,
    onScreenView: (AnalyticsScreen) -> Unit,
) {
    val currentOnScreenView by rememberUpdatedState(onScreenView)
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(state, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            snapshotFlow { state.currentKey }.collect { key ->
                key.analyticsScreen()?.let(currentOnScreenView)
            }
        }
    }
}

private fun NavKey.analyticsScreen(): AnalyticsScreen? = catalogAnalyticsScreen()
    ?: cartAnalyticsScreen()
    ?: promoAnalyticsScreen()
    ?: checkoutAnalyticsScreen()
    ?: settingsAnalyticsScreen()
    ?: AnalyticsScreen.ProductDetails.takeIf { this is CartProductRoute }
