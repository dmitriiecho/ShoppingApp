package krio.systemdesign.shoppingapp.navigation.transitions

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.contains
import androidx.navigation3.runtime.get
import androidx.navigation3.scene.Scene
import krio.systemdesign.shoppingapp.core.composeutils.animation.SharedElementScreenKey
import krio.systemdesign.shoppingapp.shared.ui.product.PRODUCT_IMAGE_TRANSITION_MILLIS

// No transition: a fade made the app feel slow. Screens fade only where the product image flies,
// in step with it (PRODUCT_IMAGE_TRANSITION_MILLIS). The same for forward, back and the predictive back gesture.
internal fun AnimatedContentTransitionScope<Scene<NavKey>>.appTransition(): ContentTransform =
    if (isProductImageTransition()) {
        fadeIn(tween(PRODUCT_IMAGE_TRANSITION_MILLIS)) togetherWith fadeOut(tween(PRODUCT_IMAGE_TRANSITION_MILLIS))
    } else {
        EnterTransition.None togetherWith ExitTransition.None
    }

// Where the product image flies: between two screens marked with sharedElementScreen() in the same tab
// (the catalog list and product details, the cart and a product opened from it).
private fun AnimatedContentTransitionScope<Scene<NavKey>>.isProductImageTransition(): Boolean {
    val from = initialState.metadata
    val to = targetState.metadata
    val bothShowImages = SharedElementScreenKey in from && SharedElementScreenKey in to
    return bothShowImages && from[TabKey] != null && from[TabKey] == to[TabKey]
}
