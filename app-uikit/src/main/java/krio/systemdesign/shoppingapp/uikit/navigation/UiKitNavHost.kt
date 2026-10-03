package krio.systemdesign.shoppingapp.uikit.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import krio.systemdesign.shoppingapp.uikit.SectionListScreen
import krio.systemdesign.shoppingapp.uikit.SectionScreen
import krio.systemdesign.shoppingapp.uikit.UiKitSection
import kotlinx.serialization.Serializable

@Serializable
private data object SectionListRoute

@Serializable
private data class SectionRoute(val section: UiKitSection)

// Два экрана: список разделов и раздел с примерами.
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
        // Экраны сменяются сразу, без анимации: плавная смена по умолчанию в NavHost делала переходы медленными.
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
                // navigateUp, а не popBackStack: двойное нажатие «Назад» не уберёт список разделов и не оставит пустой экран.
                onBack = { navController.navigateUp() },
            )
        }
    }
}
