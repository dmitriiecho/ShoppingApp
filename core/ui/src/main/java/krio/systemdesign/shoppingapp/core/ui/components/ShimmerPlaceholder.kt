package krio.systemdesign.shoppingapp.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer

// Заглушка на время загрузки. В content раскладываются фигуры (Modifier.shimmerShape) по размерам настоящего
// содержимого, а заглушка закрашивает их ровным серым, и по ним раз за разом проходит светлая полоса слева направо.
// Полоса одна на всю заглушку, поэтому по соседним фигурам она идёт как по одной.
// isAnimating = false — полоса стоит, остаётся только серое: например, когда загрузка не удалась.
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
            // Отдельный слой нужен для BlendMode.SrcIn: он красит только там, где фигуры уже нарисованы.
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            // Время кадра читается только при рисовании: каждый кадр перерисовывает заглушку без перекомпоновки.
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

// Фигура внутри ShimmerPlaceholder. Цвет у неё неважен: заглушка всё равно перекрашивает её, важна только форма.
fun Modifier.shimmerShape(shape: Shape): Modifier = clip(shape).background(Color.Black)

private const val SHIMMER_DURATION_MS = 1200L
