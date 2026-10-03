package krio.systemdesign.shoppingapp.core.ui.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.components.buttons.CartQuantityControlPlaceholder
import krio.systemdesign.shoppingapp.core.ui.components.images.ProductImage
import krio.systemdesign.shoppingapp.core.ui.components.loading.ShimmerPlaceholder
import krio.systemdesign.shoppingapp.core.ui.components.loading.shimmerShape

// Карточка товара в списке: картинка, название и цена, под ними content — например, кнопки корзины.
// Одна и та же в каталоге и в корзине, а что положить вниз, решает экран.
@Composable
fun ProductCard(
    name: String,
    imageUrl: String,
    price: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    // Цена за штуку. Показывается над price, когда price — сумма за несколько штук, как в корзине.
    unitPrice: String? = null,
    // Ключ общего элемента картинки: с ним она перелетает на карточку товара (см. ProductImage).
    sharedElementKey: Any? = null,
    // Приглушить картинку, название и цену, например у закончившегося товара:
    // так его видно сразу, даже не читая плашку. Содержимое снизу не приглушается.
    isDimmed: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PRODUCT_CARD_PADDING),
            verticalArrangement = Arrangement.spacedBy(PRODUCT_CARD_PADDING),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(if (isDimmed) DIMMED_ALPHA else 1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProductImage(
                    imageUrl = imageUrl,
                    contentDescription = name,
                    modifier = Modifier.size(PRODUCT_CARD_IMAGE_SIZE),
                    sharedElementKey = sharedElementKey,
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(8.dp))
                    if (unitPrice != null) {
                        Text(
                            text = unitPrice,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        text = price,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            content()
        }
    }
}

// Заглушка карточки товара с кнопками корзины на время загрузки. Повторяет размеры ProductCard без unitPrice,
// чтобы список не прыгал, когда заглушка сменяется товаром.
// isLoading = false — блик стоит, остаётся только серое: например, когда загрузка не удалась.
@Composable
fun ProductCardPlaceholder(
    modifier: Modifier = Modifier,
    isLoading: Boolean = true,
) {
    AppCard(modifier = modifier.fillMaxWidth()) {
        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PRODUCT_CARD_PADDING),
            isAnimating = isLoading,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(PRODUCT_CARD_PADDING)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(PRODUCT_CARD_IMAGE_SIZE)
                            .shimmerShape(MaterialTheme.shapes.medium),
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(20.dp)
                                .shimmerShape(MaterialTheme.shapes.extraSmall),
                        )
                        Spacer(Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .width(96.dp)
                                .height(20.dp)
                                .shimmerShape(MaterialTheme.shapes.extraSmall),
                        )
                    }
                }
                CartQuantityControlPlaceholder()
            }
        }
    }
}

private val PRODUCT_CARD_PADDING = 12.dp
private val PRODUCT_CARD_IMAGE_SIZE = 88.dp

// Прозрачность приглушённой части карточки.
private const val DIMMED_ALPHA = 0.5f
