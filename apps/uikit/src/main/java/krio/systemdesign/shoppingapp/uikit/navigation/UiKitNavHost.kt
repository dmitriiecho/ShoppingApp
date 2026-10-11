package krio.systemdesign.shoppingapp.uikit.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import krio.systemdesign.shoppingapp.uikit.screens.GroupListScreen
import krio.systemdesign.shoppingapp.uikit.screens.SectionListScreen
import krio.systemdesign.shoppingapp.uikit.screens.SectionScreen
import krio.systemdesign.shoppingapp.uikit.screens.UiKitSection

@Serializable
private data object GroupListKey : NavKey

@Serializable
private data class SectionListKey(val group: UiKitSection.Group) : NavKey

@Serializable
private data class SectionKey(val section: UiKitSection) : NavKey

@Composable
internal fun UiKitNavHost(
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backStack = rememberNavBackStack(navKeysConfiguration, GroupListKey)
    NavDisplay(
        entries = entries(backStack, darkTheme, onToggleTheme),
        modifier = modifier,
        onBack = { backStack.goBack() },
        // No transition animation: the default fade made switching feel slow.
        transitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
        popTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
        predictivePopTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
    )
}

@Composable
private fun entries(
    backStack: NavBackStack<NavKey>,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
) = rememberDecoratedNavEntries(
    backStack = backStack,
    entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),
    entryProvider = entryProvider {
        entry<GroupListKey> {
            GroupListScreen(
                darkTheme = darkTheme,
                onToggleTheme = onToggleTheme,
                onOpenGroup = { backStack.add(SectionListKey(it)) },
            )
        }
        entry<SectionListKey> { key ->
            SectionListScreen(
                group = key.group,
                darkTheme = darkTheme,
                onToggleTheme = onToggleTheme,
                onOpenSection = { backStack.add(SectionKey(it)) },
                onBack = { backStack.goBack() },
            )
        }
        entry<SectionKey> { key ->
            SectionScreen(
                section = key.section,
                darkTheme = darkTheme,
                onToggleTheme = onToggleTheme,
                onBack = { backStack.goBack() },
            )
        }
    },
)

// Never removes the first screen: a double Back tap won't leave a blank screen.
private fun NavBackStack<NavKey>.goBack() {
    if (size > 1) removeAt(lastIndex)
}

private val navKeysConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(GroupListKey::class)
            subclass(SectionListKey::class)
            subclass(SectionKey::class)
        }
    }
}
