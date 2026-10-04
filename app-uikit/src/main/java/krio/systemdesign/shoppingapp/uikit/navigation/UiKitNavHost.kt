package krio.systemdesign.shoppingapp.uikit.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import krio.systemdesign.shoppingapp.uikit.screens.SectionListScreen
import krio.systemdesign.shoppingapp.uikit.screens.SectionScreen
import krio.systemdesign.shoppingapp.uikit.screens.UiKitSection
import kotlinx.serialization.Serializable

@Serializable
private data object SectionListRoute

@Serializable
private data class SectionRoute(val section: UiKitSection)

@Composable
fun UiKitNavHost(
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = SectionListRoute,
        modifier = modifier,
        // No transition animation: NavHost's default fade made switching feel slow.
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
    ) {
        composable<SectionListRoute> {
            SectionListScreen(
                darkTheme = darkTheme,
                onToggleTheme = onToggleTheme,
                onOpenSection = { navController.navigate(SectionRoute(it)) },
            )
        }
        composable<SectionRoute> { entry ->
            SectionScreen(
                section = entry.toRoute<SectionRoute>().section,
                darkTheme = darkTheme,
                onToggleTheme = onToggleTheme,
                // navigateUp, not popBackStack: a double Back tap won't pop the section list and leave a blank screen.
                onBack = { navController.navigateUp() },
            )
        }
    }
}
