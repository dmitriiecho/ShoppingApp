package krio.systemdesign.shoppingapp.navigation.transitions

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.contains
import androidx.navigation3.runtime.get
import androidx.navigation3.runtime.metadata
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import krio.systemdesign.shoppingapp.core.composeutils.animation.LocalNavAnimatedVisibilityScope
import krio.systemdesign.shoppingapp.core.composeutils.animation.SharedElementScreenKey
import krio.systemdesign.shoppingapp.navigation.bottombar.BottomTab

// The tab a screen belongs to, added by the app to every entry of a tab's back stack; none over the tabs.
object TabKey : NavMetadataKey<BottomTab>

internal fun NavEntry<NavKey>.withTab(
    key: NavKey,
    tab: BottomTab,
): NavEntry<NavKey> = NavEntry(key, contentKey, metadata + metadata { put(TabKey, tab) }) { Content() }

// Gives the screens marked with sharedElementScreen() the animation of their entry, so their shared elements fly.
// Also notes the tab of each entry on screen: two tabs at once mean the tab is switching (both screens are visible
// during the transition), and then the app turns shared elements off, so an image doesn't fly between tabs.
internal fun sharedElementsDecorator(tabsOnScreen: MutableList<BottomTab>) = NavEntryDecorator<NavKey> { entry ->
    val tab = entry.metadata[TabKey]
    if (tab != null) {
        DisposableEffect(tab) {
            tabsOnScreen.add(tab)
            onDispose { tabsOnScreen.remove(tab) }
        }
    }
    if (SharedElementScreenKey in entry.metadata) {
        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides LocalNavAnimatedContentScope.current) {
            entry.Content()
        }
    } else {
        entry.Content()
    }
}

// The leaving screen still takes taps: a double tap on Back closed two screens. NavDisplay keeps an entry below
// RESUMED until its transition ends; checked on each touch, not on recomposition, as a second tap can come first.
internal val blockTouchesUntilResumed = NavEntryDecorator<NavKey> { entry ->
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    Box(
        modifier = Modifier.pointerInput(lifecycle) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    if (lifecycle.currentState != Lifecycle.State.RESUMED) {
                        event.changes.forEach { it.consume() }
                    }
                }
            }
        },
        propagateMinConstraints = true,
    ) {
        entry.Content()
    }
}
