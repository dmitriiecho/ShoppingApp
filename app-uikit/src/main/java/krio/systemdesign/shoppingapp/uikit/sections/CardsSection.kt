package krio.systemdesign.shoppingapp.uikit.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.components.AppCard
import krio.systemdesign.shoppingapp.core.ui.components.AppListItem
import krio.systemdesign.shoppingapp.core.ui.components.CartQuantityControl
import krio.systemdesign.shoppingapp.core.ui.components.Notice
import krio.systemdesign.shoppingapp.core.ui.components.OrderItemRow
import krio.systemdesign.shoppingapp.core.ui.components.OrderTotals
import krio.systemdesign.shoppingapp.core.ui.components.OutOfStockButton
import krio.systemdesign.shoppingapp.core.ui.components.ProductCard
import krio.systemdesign.shoppingapp.core.ui.components.PromoCodeCoupon
import krio.systemdesign.shoppingapp.core.ui.components.SectionCard
import krio.systemdesign.shoppingapp.core.ui.components.SingleChoiceButtons
import krio.systemdesign.shoppingapp.core.ui.text.formatPrice
import krio.systemdesign.shoppingapp.uikit.R
import krio.systemdesign.shoppingapp.uikit.components.SampleData
import krio.systemdesign.shoppingapp.uikit.components.SampleList
import krio.systemdesign.shoppingapp.uikit.components.SampleVariant
import krio.systemdesign.shoppingapp.uikit.components.sampleGroup

@Composable
fun CardsSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        sampleGroup("AppCard") {
            SampleVariant {
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    CardText()
                }
            }
            SampleVariant(stringResource(R.string.uikit_variant_clickable)) {
                AppCard(modifier = Modifier.fillMaxWidth(), onClick = {}) {
                    CardText()
                }
            }
        }
        sampleGroup("ProductCard") {
            SampleVariant(stringResource(R.string.uikit_variant_catalog)) {
                var quantity by rememberSaveable { mutableIntStateOf(0) }
                ProductCard(
                    name = stringResource(R.string.uikit_sample_product_headphones),
                    imageUrl = SampleData.headphonesImageUrl,
                    price = formatPrice(SampleData.HEADPHONES_PRICE),
                    onClick = {},
                ) {
                    CartQuantityControl(
                        quantity = quantity,
                        onAdd = { quantity = 1 },
                        onIncrease = { quantity++ },
                        onDecrease = { quantity-- },
                        onRemoveAll = { quantity = 0 },
                    )
                }
            }
            SampleVariant(stringResource(R.string.uikit_variant_catalog_out_of_stock)) {
                ProductCard(
                    name = stringResource(R.string.uikit_sample_product_keyboard),
                    imageUrl = SampleData.keyboardImageUrl,
                    price = formatPrice(SampleData.KEYBOARD_PRICE),
                    onClick = {},
                ) {
                    OutOfStockButton()
                }
            }
            SampleVariant(stringResource(R.string.uikit_variant_cart_dimmed)) {
                ProductCard(
                    name = stringResource(R.string.uikit_sample_product_keyboard),
                    imageUrl = SampleData.keyboardImageUrl,
                    price = formatPrice(SampleData.KEYBOARD_PRICE * 2),
                    onClick = {},
                    unitPrice = formatPrice(SampleData.KEYBOARD_PRICE),
                    isDimmed = true,
                ) {
                    Notice(
                        icon = Icons.Outlined.Inventory2,
                        title = stringResource(R.string.uikit_sample_out_of_stock),
                        accentColor = MaterialTheme.colorScheme.error,
                    )
                    CartQuantityControl(
                        quantity = 2,
                        onAdd = {},
                        onIncrease = {},
                        onDecrease = {},
                        onRemoveAll = {},
                        canIncrease = false,
                    )
                }
            }
        }
        sampleGroup("SectionCard") {
            SampleVariant(stringResource(R.string.uikit_variant_as_in_checkout)) {
                SectionCard(
                    icon = Icons.Outlined.ShoppingBag,
                    title = stringResource(R.string.uikit_sample_order_items),
                ) {
                    SampleOrderItems()
                }
            }
            SampleVariant(stringResource(R.string.uikit_variant_with_subtitle)) {
                SectionCard(
                    icon = Icons.Outlined.ConfirmationNumber,
                    title = stringResource(R.string.uikit_sample_promo_codes),
                    subtitle = stringResource(R.string.uikit_sample_promo_codes_hint),
                ) {
                    SampleCoupons()
                }
            }
        }
        sampleGroup("OrderItemRow") {
            SampleVariant {
                SampleOrderItems()
            }
        }
        sampleGroup("PromoCodeCoupon") {
            SampleVariant {
                SampleCoupons()
            }
        }
        sampleGroup("OrderTotals") {
            SampleVariant(stringResource(R.string.uikit_variant_with_discount)) {
                val subtotal = SampleData.HEADPHONES_PRICE
                val discount = subtotal * SampleData.PROMO_DISCOUNT_PERCENT / 100
                OrderTotals(
                    subtotal = formatPrice(subtotal),
                    total = formatPrice(subtotal - discount),
                    discount = "−${formatPrice(discount)}",
                )
            }
            SampleVariant(stringResource(R.string.uikit_variant_without_discount)) {
                OrderTotals(
                    subtotal = formatPrice(SampleData.HEADPHONES_PRICE),
                    total = formatPrice(SampleData.HEADPHONES_PRICE),
                )
            }
        }
        // У AppListItem свои отступы, как у строк списка на экране, поэтому рамка их не добавляет.
        sampleGroup("AppListItem") {
            SampleVariant(stringResource(R.string.uikit_variant_trailing_icon), contentPadding = 0.dp) {
                AppListItem(
                    icon = Icons.Default.Link,
                    title = stringResource(R.string.uikit_sample_deep_links),
                    description = stringResource(R.string.uikit_sample_deep_links_description),
                    onClick = {},
                    trailing = { Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null) },
                )
            }
            SampleVariant(stringResource(R.string.uikit_variant_trailing_choice), contentPadding = 0.dp) {
                var selectedSeconds by rememberSaveable { mutableIntStateOf(0) }
                AppListItem(
                    icon = Icons.Outlined.HourglassEmpty,
                    title = stringResource(R.string.uikit_sample_request_delay),
                    description = stringResource(R.string.uikit_sample_seconds, selectedSeconds),
                    trailing = {
                        SingleChoiceButtons(
                            options = SampleData.DELAY_SECONDS,
                            selected = selectedSeconds,
                            onSelect = { selectedSeconds = it },
                            modifier = Modifier.width(168.dp),
                        ) { seconds ->
                            Text(seconds.toString())
                        }
                    },
                )
            }
        }
    }
}

// Две строки заказа, как на экране оформления: одна штука и две.
@Composable
private fun SampleOrderItems() {
    OrderItemRow(
        name = stringResource(R.string.uikit_sample_product_headphones),
        imageUrl = SampleData.headphonesImageUrl,
        quantity = 1,
        unitPrice = formatPrice(SampleData.HEADPHONES_PRICE),
        total = formatPrice(SampleData.HEADPHONES_PRICE),
    )
    OrderItemRow(
        name = stringResource(R.string.uikit_sample_product_keyboard),
        imageUrl = SampleData.keyboardImageUrl,
        quantity = 2,
        unitPrice = formatPrice(SampleData.KEYBOARD_PRICE),
        total = formatPrice(SampleData.KEYBOARD_PRICE * 2),
    )
}

// Промокоды с сервера, как на экране промокода.
@Composable
private fun SampleCoupons() {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PromoCodeCoupon(
            code = SampleData.PROMO_CODE,
            discountPercent = SampleData.PROMO_DISCOUNT_PERCENT,
            onClick = {},
        )
        PromoCodeCoupon(
            code = SampleData.SECOND_PROMO_CODE,
            discountPercent = SampleData.SECOND_PROMO_DISCOUNT_PERCENT,
            onClick = {},
        )
    }
}

@Composable
private fun CardText() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = stringResource(R.string.uikit_sample_card_content),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
