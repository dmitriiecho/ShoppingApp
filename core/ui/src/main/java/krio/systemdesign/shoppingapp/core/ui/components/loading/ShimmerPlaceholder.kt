package krio.systemdesign.shoppingapp.core.ui.components.loading

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.components.buttons.CartQuantityControlPlaceholder
import krio.systemdesign.shoppingapp.core.ui.components.cards.PromoCodeCouponPlaceholder
import krio.systemdesign.shoppingapp.core.ui.theme.ShoppingAppTheme

@Composable
fun ShimmerPlaceholder(
    modifier: Modifier = Modifier,
    isAnimating: Boolean = true,
    content: @Composable () -> Unit,
) {
    val baseColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    val highlightColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.16f)
    val frameTimeMillis by produceState(0L, isAnimating) {
        if (isAnimating) {
            while (true) withFrameMillis { value = it }
        }
    }

    Box(
        modifier = modifier
            // A separate layer for BlendMode.SrcIn, so the band paints only over the shapes.
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            // Frame time is read only in draw, so frames redraw without recomposition.
            .drawWithContent {
                drawContent()
                if (!isAnimating) {
                    drawRect(color = baseColor, blendMode = BlendMode.SrcIn)
                    return@drawWithContent
                }
                val progress = (frameTimeMillis % SHIMMER_DURATION_MS) / SHIMMER_DURATION_MS.toFloat()
                val bandHalfWidth = size.width * 0.4f
                val bandCenter = -bandHalfWidth + progress * (size.width + 2 * bandHalfWidth)
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(baseColor, highlightColor, baseColor),
                        startX = bandCenter - bandHalfWidth,
                        endX = bandCenter + bandHalfWidth,
                    ),
                    blendMode = BlendMode.SrcIn,
                )
            },
    ) {
        content()
    }
}

// A shape inside ShimmerPlaceholder. Only the shape matters: the placeholder repaints its color.
fun Modifier.shimmerShape(shape: Shape): Modifier = clip(shape).background(Color.Black)

private const val SHIMMER_DURATION_MS = 1200L

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ShimmerPlaceholderPreview() {
    ShoppingAppTheme {
        Surface {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .shimmerShape(RectangleShape),
                    )
                }
                ShimmerPlaceholder(modifier = Modifier.fillMaxWidth()) {
                    ProductInfoPlaceholder()
                }
                ShimmerPlaceholder(modifier = Modifier.fillMaxWidth()) {
                    CartQuantityControlPlaceholder()
                }
                ShimmerPlaceholder {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(2) {
                            PromoCodeCouponPlaceholder()
                        }
                    }
                }
            }
        }
    }
}
