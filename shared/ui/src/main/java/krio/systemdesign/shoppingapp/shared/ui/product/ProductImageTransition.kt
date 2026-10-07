package krio.systemdesign.shoppingapp.shared.ui.product

import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import krio.systemdesign.shoppingapp.core.composeutils.animation.LocalNavAnimatedVisibilityScope
import krio.systemdesign.shoppingapp.core.composeutils.animation.LocalSharedTransitionScope

// One timing for everything in the product image transition: the image, its corners and the screens' fade,
// so they all finish together. A fade with its own timing would finish before or after the image lands.
// A tween, not a spring: a spring does most of a fade in the first 100 ms, so the screens seem to swap at once.
const val PRODUCT_IMAGE_TRANSITION_MILLIS = 400

// ProductImage applies these. On screen the background (tile) and the photo (image) slide; cornerRadius is the
// background's roundness right now.
internal class ProductImageSharedElement(
    val tile: Modifier,
    val image: Modifier,
    val cornerRadius: Dp,
)

// The photo slides between the list and the product screen instead of popping in. Two sharedElements: padding is
// 2 dp in the list and 32 dp on the product screen, so the photo can't ride inside the background. No key or
// nav scopes: it stays put.
@Composable
internal fun productImageSharedElement(
    key: ProductImageKey?,
    cornerRadius: Dp,
): ProductImageSharedElement {
    // Corners start as in the list. Read once from tileCornerRadii, before this screen overwrites that entry.
    val startCornerRadius = remember(key) { key?.let { tileCornerRadii[it]?.value } }
    if (key != null) {
        DisposableEffect(key, cornerRadius) {
            val entry = TileCornerRadius(cornerRadius)
            tileCornerRadii[key] = entry
            onDispose {
                // This background leaves the screen. Remove only its entry: the other screen may have replaced it.
                if (tileCornerRadii[key] === entry) tileCornerRadii.remove(key)
            }
        }
    }

    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
    if (key == null || sharedTransitionScope == null || animatedVisibilityScope == null) {
        return ProductImageSharedElement(tile = Modifier, image = Modifier, cornerRadius = cornerRadius)
    }
    // Corners morph with the slide, same timing as the move, so they don't jump.
    val animatedCornerRadius by animatedVisibilityScope.transition.animateDp(
        transitionSpec = { tween(PRODUCT_IMAGE_TRANSITION_MILLIS) },
        label = "productImageCornerRadius",
    ) { state ->
        // Opening's first frame (PreEnter) still uses the list's corners, then this screen's cornerRadius.
        if (state == EnterExitState.PreEnter && startCornerRadius != null) startCornerRadius else cornerRadius
    }
    return with(sharedTransitionScope) {
        ProductImageSharedElement(
            // Background slides. Only the opening screen's tile is drawn: both would be see-through.
            tile = Modifier.sharedElement(
                sharedContentState = rememberSharedContentState(SharedTileKey(key)),
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = ProductImageBoundsTransform,
            ),
            // Photo slides above the background: zIndexInOverlay draws it over the tile.
            image = Modifier.sharedElement(
                sharedContentState = rememberSharedContentState(SharedImageKey(key)),
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = ProductImageBoundsTransform,
                zIndexInOverlay = 1f,
            ),
            cornerRadius = animatedCornerRadius,
        )
    }
}

private val ProductImageBoundsTransform = BoundsTransform { _, _ -> tween(PRODUCT_IMAGE_TRANSITION_MILLIS) }

// Corner radius of each visible background. The opening screen reads the list's value from here.
private val tileCornerRadii = mutableMapOf<ProductImageKey, TileCornerRadius>()

// One object per background, so onDispose removes only that entry.
private class TileCornerRadius(val value: Dp)

// Pairs the two backgrounds. sharedElement matches screens that pass an equal key.
private data class SharedTileKey(val key: ProductImageKey)

// Pairs the two photos. Not SharedTileKey: same product id would match the photo to the background.
private data class SharedImageKey(val key: ProductImageKey)
