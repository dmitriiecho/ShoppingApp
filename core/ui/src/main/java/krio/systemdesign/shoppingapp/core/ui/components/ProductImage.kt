package krio.systemdesign.shoppingapp.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

// Картинка товара. У картинок с сервера прозрачный фон, поэтому под ней своя плитка:
// светлое пятно в центре и чуть тонированные края, как на студийной фотографии.
// Товар вписывается целиком, с отступом от краёв плитки.
@Composable
fun ProductImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    // У самих картинок поля уже есть, поэтому в маленькой плитке отступ почти не нужен.
    contentPadding: Dp = 2.dp,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(productImageBrush()),
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentScale = ContentScale.Fit,
        )
    }
}

// Цвета берутся из темы. Тёмная тема или светлая, определяется по фону самой схемы.
// В светлой: белый центр и чуть тонированные края.
// В тёмной вся плитка светлее карточки (у AppCard фон surfaceContainerHigh), а центр — заметно:
// иначе плитка теряется на карточке, а чёрные товары (наушники, клавиатура) сливаются с фоном.
@Composable
private fun productImageBrush(): Brush {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f
    val center: Color
    val edge: Color
    if (isDark) {
        center = lerp(colors.surfaceContainerHighest, Color.White, 0.30f)
        edge = lerp(lerp(colors.surfaceContainerHighest, Color.White, 0.06f), colors.primary, 0.03f)
    } else {
        center = Color.White
        edge = lerp(colors.surfaceContainerHigh, colors.primary, 0.04f)
    }
    return Brush.radialGradient(listOf(center, edge))
}
