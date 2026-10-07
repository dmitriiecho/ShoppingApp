package krio.systemdesign.shoppingapp.navigation

import android.net.Uri
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import krio.systemdesign.shoppingapp.core.composeutils.animation.LocalSharedTransitionScope
import krio.systemdesign.shoppingapp.navigation.bottombar.AppBottomBar
import krio.systemdesign.shoppingapp.navigation.bottombar.BottomNavRoutes
import krio.systemdesign.shoppingapp.navigation.bottombar.bottomTab
import krio.systemdesign.shoppingapp.navigation.transitions.appEnterTransition
import krio.systemdesign.shoppingapp.navigation.transitions.appExitTransition
import krio.systemdesign.shoppingapp.navigation.transitions.blockTouchesDuringTransitions
import krio.systemdesign.shoppingapp.shared.analytics.event.AnalyticsScreen

@Composable
fun AppNavHost(
    deepLinks: Flow<Uri>,
    onScreenView: (AnalyticsScreen) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val isSwitchingTabs by navController.isSwitchingTabsAsState()

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = { AppBottomBar(navController) },
    ) { innerPadding ->
        // The layer shared elements fly over, e.g. a product image from the list to the product screen.
        SharedTransitionLayout(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        ) {
            // Only within a tab: otherwise a product that is both in the catalog and the cart would fly between tabs.
            CompositionLocalProvider(LocalSharedTransitionScope provides this.takeUnless { isSwitchingTabs }) {
                NavHost(
                    navController = navController,
                    startDestination = BottomNavRoutes.CatalogTab,
                    modifier = Modifier.blockTouchesDuringTransitions(navController),
                    enterTransition = { appEnterTransition() },
                    exitTransition = { appExitTransition() },
                ) {
                    appGraph(navController)
                }
            }
        }

        // Must follow NavHost: links need the graph it sets.
        LaunchedEffect(navController, deepLinks) {
            deepLinks.collect { navController.openDeepLink(it) }
        }

        ObserveScreenViews(navController, onScreenView)
    }
}

// True while one tab replaces another: shared elements are off then, so images don't fly between tabs.
// During a transition both screens are visible; screens from different tabs mean the tab is switching.
@Composable
private fun NavHostController.isSwitchingTabsAsState(): State<Boolean> = remember(this) {
    visibleEntries.map { entries ->
        val tabs = entries.mapNotNull { it.destination.bottomTab()?.id }.toSet()
        tabs.size > 1
    }
}.collectAsState(initial = false)
