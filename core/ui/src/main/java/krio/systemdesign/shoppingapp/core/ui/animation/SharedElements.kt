package krio.systemdesign.shoppingapp.core.ui.animation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf

// Shared element: the same element on two screens (e.g. the product image in the list and on the product details
// screen) that flies from one screen to the other during navigation. Modifier.sharedElement needs two scopes.
// They are created in navigation, but used deep inside screens (ProductImage), so they are passed via
// CompositionLocal instead of parameters through every screen. Null means the element does not fly,
// it just appears and disappears with its screen.

// The layer elements fly over. Provided by AppNavHost around NavHost.
// Null while tabs are switching, so that an image does not fly from one tab to another.
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

// The enter and exit animation of the current screen. Provided by a screen in the navigation graph
// (catalog, cart) whose elements should fly. Null on other screens and outside navigation (UI kit).
val LocalNavAnimatedVisibilityScope = staticCompositionLocalOf<AnimatedVisibilityScope?> { null }
