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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.result.ResultEventBus
import androidx.navigation3.runtime.result.rememberResultEventBus
import androidx.navigation3.runtime.result.rememberResultEventBusNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.flow.Flow
import krio.systemdesign.shoppingapp.core.composeutils.animation.LocalSharedTransitionScope
import krio.systemdesign.shoppingapp.navigation.bottombar.AppBottomBar
import krio.systemdesign.shoppingapp.navigation.bottombar.BottomTab
import krio.systemdesign.shoppingapp.navigation.transitions.appTransition
import krio.systemdesign.shoppingapp.navigation.transitions.blockTouchesUntilResumed
import krio.systemdesign.shoppingapp.navigation.transitions.sharedElementsDecorator
import krio.systemdesign.shoppingapp.navigation.transitions.withTab
import krio.systemdesign.shoppingapp.shared.analytics.event.AnalyticsScreen

@Composable
fun AppNavHost(
    deepLinks: Flow<Uri>,
    onScreenView: (AnalyticsScreen) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberNavigationState()
    val navigator = remember(state) { AppNavigator(state) }
    val resultEventBus = rememberResultEventBus()
    val entryProvider = remember(navigator, resultEventBus) {
        entryProvider<NavKey> { appEntries(navigator, resultEventBus) }
    }
    val tabsOnScreen = remember { mutableStateListOf<BottomTab>() }
    val sharedElements = remember(tabsOnScreen) { sharedElementsDecorator(tabsOnScreen) }

    // Each back stack keeps its own state and ViewModels, so a tab the user left keeps its screens.
    val tabEntries = BottomTab.entries.associateWith { tab ->
        rememberAppEntries(state.tabStacks.getValue(tab), resultEventBus, sharedElements) { key ->
            entryProvider(key).withTab(key, tab)
        }
    }
    val overTabsEntries = rememberAppEntries(state.overTabs, resultEventBus, sharedElements, entryProvider)

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            // No bottom bar over the tabs (checkout).
            if (state.overTabs.isEmpty()) {
                AppBottomBar(currentTab = state.currentTab, onTabClick = navigator::switchTab)
            }
        },
    ) { innerPadding ->
        // The layer shared elements fly over, e.g. a product image from the list to the product screen.
        SharedTransitionLayout(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        ) {
            // Only within a tab: otherwise a product that is both in the catalog and the cart would fly between tabs.
            val isSwitchingTabs = tabsOnScreen.distinct().size > 1
            CompositionLocalProvider(LocalSharedTransitionScope provides this.takeUnless { isSwitchingTabs }) {
                // Not sharedTransitionScope = this: NavDisplay would then fly whole screens between scenes.
                NavDisplay(
                    entries = tabEntries.getValue(state.currentTab) + overTabsEntries,
                    onBack = navigator::goBack,
                    transitionSpec = { appTransition() },
                    popTransitionSpec = { appTransition() },
                    predictivePopTransitionSpec = { appTransition() },
                )
            }
        }

        LaunchedEffect(navigator, deepLinks) {
            deepLinks.collect { navigator.openDeepLink(it) }
        }

        ObserveScreenViews(state, onScreenView)
    }
}

@Composable
private fun rememberAppEntries(
    backStack: NavBackStack<NavKey>,
    resultEventBus: ResultEventBus,
    sharedElements: NavEntryDecorator<NavKey>,
    entryProvider: (NavKey) -> NavEntry<NavKey>,
): List<NavEntry<NavKey>> = rememberDecoratedNavEntries(
    backStack = backStack,
    entryDecorators = listOf(
        // Must come first: the other decorators save state through it.
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator(),
        rememberResultEventBusNavEntryDecorator(resultEventBus),
        sharedElements,
        blockTouchesUntilResumed,
    ),
    entryProvider = entryProvider,
)
