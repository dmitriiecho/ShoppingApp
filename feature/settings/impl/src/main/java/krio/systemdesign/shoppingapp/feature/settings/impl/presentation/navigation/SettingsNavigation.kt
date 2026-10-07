package krio.systemdesign.shoppingapp.feature.settings.impl.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import krio.systemdesign.shoppingapp.feature.settings.impl.presentation.settings.SettingsScreen

class SettingsNavigationScope internal constructor(internal val builder: NavGraphBuilder)

val NavGraphBuilder.settings: SettingsNavigationScope
    get() = SettingsNavigationScope(this)

// onClose exits the whole settings feature. Settings can be the root of a tab, where there is nothing to close,
// or it can be embedded in a flow the user returns back from.
fun SettingsNavigationScope.graph(
    navController: NavController,
    onClose: () -> Unit,
) {
    builder.navigation<SettingsRoutes.Graph>(
        startDestination = SettingsRoutes.Settings,
    ) {
        composable<SettingsRoutes.Settings> {
            SettingsScreen(
                onBack = onClose,
            )
        }
    }
}
