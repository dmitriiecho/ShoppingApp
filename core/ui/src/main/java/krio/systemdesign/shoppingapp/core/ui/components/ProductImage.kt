package krio.systemdesign.shoppingapp.core.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter

// Картинка товара. У картинок с сервера прозрачный фон, поэтому под ней своя плитка:
// светлое пятно в центре и чуть тонированные края, как на студийной фотографии.
// Товар вписывается целиком, с отступом от краёв плитки.
// Пока картинка грузится, по плитке бежит блик (шиммер). Если загрузить не удалось, остаётся пустая плитка.
@Composable
fun ProductImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    // У самих картинок поля уже есть, поэтому в маленькой плитке отступ почти не нужен.
    contentPadding: Dp = 2.dp,
) {
    val colors = productImageColors()
    var isLoading by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .clip(shape)
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
            model = imageUrl,
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentScale = ContentScale.Fit,
            onState = { isLoading = it is AsyncImagePainter.State.Loading },
        )
    }
}

private class ProductImageColors(val center: Color, val edge: Color)

// Цвета берутся из темы. Тёмная тема или светлая, определяется по фону самой схемы.
// В светлой: белый центр и чуть тонированные края.
// В тёмной вся плитка светлее карточки (у AppCard фон surfaceContainerHigh), а центр — заметно:
// иначе плитка теряется на карточке, а чёрные товары (наушники, клавиатура) сливаются с фоном.
@Composable
private fun productImageColors(): ProductImageColors {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f
    return if (isDark) {
        ProductImageColors(
            center = lerp(colors.surfaceContainerHighest, Color.White, 0.30f),
            edge = lerp(lerp(colors.surfaceContainerHighest, Color.White, 0.06f), colors.primary, 0.03f),
        )
    } else {
        ProductImageColors(
            center = Color.White,
            edge = lerp(colors.surfaceContainerHigh, colors.primary, 0.04f),
        )
    }
}

// Светлая полоса шириной с плитку, которая раз за разом проходит по ней слева направо.
// В начале и в конце прохода полоса целиком за краем, поэтому новый проход начинается без рывка.
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
    // progress читается только при рисовании: каждый кадр анимации перерисовывает плитку без перекомпоновки.
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
