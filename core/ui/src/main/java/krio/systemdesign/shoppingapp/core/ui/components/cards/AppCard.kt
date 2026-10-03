package krio.systemdesign.shoppingapp.core.ui.components.cards

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.theme.ShoppingAppTheme

// Карточка для товаров и блоков на экранах. Фон — ShoppingAppTheme.colors.cardContainer: он отделяет карточку
// от страницы в обеих темах (почему не стандартный фон Card — см. AppColors).
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    // Нажатие на любое место карточки. Кнопки внутри неё обрабатывают свои нажатия сами.
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val cardColors = CardDefaults.cardColors(containerColor = ShoppingAppTheme.colors.cardContainer)
    val elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier, colors = cardColors, elevation = elevation, content = content)
    } else {
        Card(modifier = modifier, colors = cardColors, elevation = elevation, content = content)
    }
}
