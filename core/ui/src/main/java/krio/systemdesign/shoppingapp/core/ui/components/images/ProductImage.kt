package krio.systemdesign.shoppingapp.core.ui.components.images

import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import coil3.compose.LocalPlatformContext
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import krio.systemdesign.shoppingapp.core.ui.icons.AppIcons
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.HeadphonesFilled
import krio.systemdesign.shoppingapp.core.ui.theme.ShapeRadius
import krio.systemdesign.shoppingapp.core.ui.theme.ShoppingAppTheme

@Composable
fun ProductImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = ShapeRadius.Medium,
    contentPadding: Dp = 2.dp,
    // Tiles with the same key on two screens fly from one to the other during navigation
    // (see LocalSharedTransitionScope).
    sharedElementKey: Any? = null,
) {
    val colors = productImageColors()
    var isLoading by remember { mutableStateOf(false) }
    val shared = productImageSharedElement(sharedElementKey, cornerRadius)
    val placeholder = rememberProductImagePlaceholder()
    val context = LocalPlatformContext.current
    val request = remember(context, imageUrl) {
        ImageRequest.Builder(context)
            .data(imageUrl)
            // Show the cached image while the right size loads, e.g. the list image on the details screen,
            // instead of an empty tile.
            .placeholderMemoryCacheKey(imageUrl)
            .build()
    }
    Box(
        modifier = modifier
            .then(shared.tile)
            .clip(RoundedCornerShape(shared.cornerRadius))
            .background(Brush.radialGradient(listOf(colors.center, colors.edge))),
    ) {
        if (isLoading) {
            Shimmer(
                base = colors.edge,
                highlight = colors.center,
                modifier = Modifier.matchParentSize(),
            )
        }
        AsyncImage(
            model = request,
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .then(shared.image),
            // Shows the placeholder on error. AsyncImage can't take both error and onState, so it's swapped in here.
            transform = { state ->
                if (state is AsyncImagePainter.State.Error) state.copy(painter = placeholder) else state
            },
            // No shimmer if the cached image is already shown.
            onState = { isLoading = it is AsyncImagePainter.State.Loading && it.painter == null },
            contentScale = ContentScale.Fit,
        )
    }
}

// The tile under the image: a light spot in the center and slightly tinted edges, like a studio photo.
private class ProductImageColors(val center: Color, val edge: Color)

@Composable
@ReadOnlyComposable
private fun productImageColors(): ProductImageColors {
    val scheme = MaterialTheme.colorScheme
    return if (ShoppingAppTheme.isDark) {
        // The whole tile is lighter than the card and the center noticeably so: otherwise the tile gets lost on the card
        // and black products (headphones, keyboard) blend into it.
        ProductImageColors(
            center = lerp(scheme.surfaceContainerHighest, Color.White, 0.30f),
            edge = lerp(lerp(scheme.surfaceContainerHighest, Color.White, 0.06f), scheme.primary, 0.03f),
        )
    } else {
        // A white center and edges with a hint of the accent.
        ProductImageColors(
            center = Color.White,
            edge = lerp(scheme.surfaceContainerHigh, scheme.primary, 0.04f),
        )
    }
}

// A light band sweeping across the tile from left to right, over and over.
// It starts and ends fully outside the tile, so sweeps loop without a jump.
@Composable
private fun Shimmer(
    base: Color,
    highlight: Color,
    modifier: Modifier = Modifier,
) {
    val progress = rememberInfiniteTransition(label = "shimmer").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 1200, easing = LinearEasing)),
        label = "shimmerProgress",
    )
    // progress is read only in draw, so animation frames redraw without recomposition.
    Spacer(
        modifier = modifier.drawBehind {
            val bandWidth = size.width
            val bandStart = -bandWidth + progress.value * (size.width + bandWidth)
            drawRect(
                Brush.horizontalGradient(
                    colors = listOf(base, highlight, base),
                    startX = bandStart,
                    endX = bandStart + bandWidth,
                ),
            )
        },
    )
}

@OptIn(ExperimentalCoilApi::class)
@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductImagePreview() {
    val loadedImage = rememberVectorPainter(AppIcons.HeadphonesFilled)
    ShoppingAppTheme {
        Surface {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf(
                    AsyncImagePreviewHandler { _, request ->
                        AsyncImagePainter.State.Success(
                            painter = loadedImage,
                            result = SuccessResult(image = ColorDrawable().asImage(), request = request),
                        )
                    },
                    AsyncImagePreviewHandler { _, request ->
                        AsyncImagePainter.State.Error(
                            painter = null,
                            result = ErrorResult(image = null, request = request, throwable = Throwable()),
                        )
                    },
                ).forEach { previewHandler ->
                    CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
                        ProductImage(
                            imageUrl = "",
                            contentDescription = "Wireless Headphones",
                            modifier = Modifier.size(88.dp),
                        )
                    }
                }
            }
        }
    }
}
