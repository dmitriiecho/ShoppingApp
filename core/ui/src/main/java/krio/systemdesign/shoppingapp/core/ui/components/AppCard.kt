package krio.systemdesign.shoppingapp.core.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp

// Карточка для товаров и блоков на экранах. В светлой теме белая на сером фоне страницы,
// в тёмной светлее фона: так карточки не сливаются со страницей.
// У стандартной Card фон surfaceContainerHighest — в светлой теме это серый, почти как фон страницы.
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    // Нажатие на любое место карточки. Кнопки внутри неё обрабатывают свои нажатия сами.
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f
    val cardColors = CardDefaults.cardColors(
        containerColor = if (isDark) colors.surfaceContainerHigh else colors.surfaceContainerLowest,
    )
    val elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier, colors = cardColors, elevation = elevation, content = content)
    } else {
        Card(modifier = modifier, colors = cardColors, elevation = elevation, content = content)
    }
}
