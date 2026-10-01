package krio.systemdesign.shoppingapp.feature.settings.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import krio.systemdesign.shoppingapp.feature.settings.presentation.settings.SettingsScreen

class SettingsNavigationScope(val builder: NavGraphBuilder)

val NavGraphBuilder.settings: SettingsNavigationScope
    get() = SettingsNavigationScope(this)

// onClose — выход из фичи настроек целиком. Настройки могут быть корнем вкладки, где закрывать некуда,
// а могут быть встроены во флоу, из которого пользователь возвращается назад.
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
