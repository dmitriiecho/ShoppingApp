package krio.systemdesign.shoppingapp.core.composeutils.animation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.metadata

// Shared element: the same element on two screens (e.g. an image in a list and on a details screen)
// that flies from one screen to the other during navigation. Modifier.sharedElement needs two scopes.
// They are created in navigation, but used deep inside screens, so they are passed via
// CompositionLocal instead of parameters through every screen. Null means the element does not fly,
// it just appears and disappears with its screen.

// The layer elements fly over. Provided by the app around NavDisplay.
// Null while tabs are switching, so that an image does not fly from one tab to another.
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

// The enter and exit animation of the current screen. Provided by the app to the screens marked with
// sharedElementScreen(). Null on other screens and outside navigation (UI kit).
val LocalNavAnimatedVisibilityScope = staticCompositionLocalOf<AnimatedVisibilityScope?> { null }

// Marks a navigation entry whose shared elements fly: entry<Key>(metadata = sharedElementScreen()) { … }.
fun sharedElementScreen(): Map<String, Any> = metadata { put(SharedElementScreenKey, true) }

object SharedElementScreenKey : NavMetadataKey<Boolean>
