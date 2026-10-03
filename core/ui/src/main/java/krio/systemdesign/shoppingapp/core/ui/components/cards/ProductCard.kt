package krio.systemdesign.shoppingapp.core.ui.components.cards

import android.content.res.Configuration
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.components.buttons.CartQuantityControl
import krio.systemdesign.shoppingapp.core.ui.components.buttons.CartQuantityControlPlaceholder
import krio.systemdesign.shoppingapp.core.ui.components.buttons.OutOfStockButton
import krio.systemdesign.shoppingapp.core.ui.components.images.ProductImage
import krio.systemdesign.shoppingapp.core.ui.components.loading.ShimmerPlaceholder
import krio.systemdesign.shoppingapp.core.ui.components.loading.shimmerShape
import krio.systemdesign.shoppingapp.core.ui.components.notices.Notice
import krio.systemdesign.shoppingapp.core.ui.components.notices.NoticeStyle
import krio.systemdesign.shoppingapp.core.ui.icons.AppIcons
import krio.systemdesign.shoppingapp.core.ui.text.formatPrice
import krio.systemdesign.shoppingapp.core.ui.theme.ShoppingAppTheme

@Composable
fun ProductCard(
    name: String,
    imageUrl: String,
    price: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    unitPrice: String? = null,
    // With this key the image flies to the product screen (see ProductImage).
    sharedElementKey: Any? = null,
    // Dims the image, name and price, e.g. for a sold-out product.
    // The content below isn't dimmed.
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
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(Modifier.height(8.dp))
                    if (unitPrice != null) {
                        Text(
                            text = unitPrice,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Text(
                        text = price,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
            content()
        }
    }
}

// Loading placeholder for a product card with cart buttons. Same size as ProductCard without unitPrice,
// so the list doesn't jump when products load. isLoading = false stops the shimmer, e.g. when loading failed.
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

// Alpha of the dimmed part of the card.
private const val DIMMED_ALPHA = 0.5f

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductCardCatalogPreview() {
    ShoppingAppTheme {
        Surface {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ProductCardPlaceholder()
                ProductCard(
                    name = "Wireless Headphones",
                    imageUrl = "",
                    price = formatPrice(14_999L),
                    onClick = {},
                ) {
                    CartQuantityControl(
                        quantity = 0,
                        onAdd = {},
                        onIncrease = {},
                        onDecrease = {},
                        onRemoveAll = {},
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                ProductCard(
                    name = "Wireless Headphones",
                    imageUrl = "",
                    price = formatPrice(14_999L),
                    onClick = {},
                ) {
                    CartQuantityControl(
                        quantity = 2,
                        onAdd = {},
                        onIncrease = {},
                        onDecrease = {},
                        onRemoveAll = {},
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                ProductCard(
                    name = "USB-C Hub",
                    imageUrl = "http://2.56.204.151:8080/images/3.png",
                    price = formatPrice(2_999L),
                    onClick = {},
                ) {
                    OutOfStockButton()
                }
            }
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductCardCartPreview() {
    ShoppingAppTheme {
        Surface {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ProductCard(
                    name = "Wireless Headphones",
                    imageUrl = "",
                    price = formatPrice(14_999L * 2),
                    onClick = {},
                    unitPrice = formatPrice(14_999L),
                ) {
                    CartQuantityControl(
                        quantity = 2,
                        onAdd = {},
                        onIncrease = {},
                        onDecrease = {},
                        onRemoveAll = {},
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductCardCartSingleIssuePreview() {
    ShoppingAppTheme {
        Surface {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ProductCard(
                    name = "USB-C Hub",
                    imageUrl = "http://2.56.204.151:8080/images/3.png",
                    price = formatPrice(2_999L),
                    onClick = {},
                    unitPrice = formatPrice(2_999L),
                    isDimmed = true,
                ) {
                    Notice(
                        icon = AppIcons.OutOfStock,
                        title = "Out of stock",
                        style = NoticeStyle.Error,
                    )
                    CartQuantityControl(
                        quantity = 1,
                        onAdd = {},
                        onIncrease = {},
                        onDecrease = {},
                        onRemoveAll = {},
                        modifier = Modifier.fillMaxWidth(),
                        canIncrease = false,
                    )
                }
                ProductCard(
                    name = "Cutting Board",
                    imageUrl = "http://2.56.204.151:8080/images/40.png",
                    price = formatPrice(4_900L),
                    onClick = {},
                    unitPrice = formatPrice(4_900L),
                ) {
                    Notice(
                        icon = AppIcons.PriceChanged,
                        title = "Price changed: now ${formatPrice(5_400L)}",
                        style = NoticeStyle.Error,
                    )
                    CartQuantityControl(
                        quantity = 1,
                        onAdd = {},
                        onIncrease = {},
                        onDecrease = {},
                        onRemoveAll = {},
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                ProductCard(
                    name = "Mechanical Keyboard",
                    imageUrl = "",
                    price = formatPrice(10_995L * 2),
                    onClick = {},
                    unitPrice = formatPrice(10_995L),
                ) {
                    Notice(
                        icon = AppIcons.NotEnoughStock,
                        title = "Only 1 available to order now",
                        style = NoticeStyle.Error,
                    )
                    CartQuantityControl(
                        quantity = 2,
                        onAdd = {},
                        onIncrease = {},
                        onDecrease = {},
                        onRemoveAll = {},
                        modifier = Modifier.fillMaxWidth(),
                        canIncrease = false,
                    )
                }
            }
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductCardCartMultipleIssuesPreview() {
    ShoppingAppTheme {
        Surface {
            ProductCard(
                name = "Notebook",
                imageUrl = "http://2.56.204.151:8080/images/32.png",
                price = formatPrice(995L * 10),
                onClick = {},
                modifier = Modifier.padding(8.dp),
                unitPrice = formatPrice(995L),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Notice(
                        icon = AppIcons.PriceChanged,
                        title = "Price changed: now ${formatPrice(1_295L)}",
                        style = NoticeStyle.Error,
                    )
                    Notice(
                        icon = AppIcons.NotEnoughStock,
                        title = "Only 5 available to order now",
                        style = NoticeStyle.Error,
                    )
                }
                CartQuantityControl(
                    quantity = 10,
                    onAdd = {},
                    onIncrease = {},
                    onDecrease = {},
                    onRemoveAll = {},
                    modifier = Modifier.fillMaxWidth(),
                    canIncrease = false,
                )
            }
        }
    }
}
