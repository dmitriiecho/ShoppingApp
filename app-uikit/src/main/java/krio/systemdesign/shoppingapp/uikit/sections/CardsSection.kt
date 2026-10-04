package krio.systemdesign.shoppingapp.uikit.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import krio.systemdesign.shoppingapp.core.ui.components.buttons.CartQuantityControl
import krio.systemdesign.shoppingapp.core.ui.components.buttons.OutOfStockButton
import krio.systemdesign.shoppingapp.core.ui.components.buttons.SingleChoiceButtons
import krio.systemdesign.shoppingapp.core.ui.components.cards.AppCard
import krio.systemdesign.shoppingapp.core.ui.components.cards.AppListItem
import krio.systemdesign.shoppingapp.core.ui.components.cards.OrderItemRow
import krio.systemdesign.shoppingapp.core.ui.components.cards.OrderTotals
import krio.systemdesign.shoppingapp.core.ui.components.cards.ProductCard
import krio.systemdesign.shoppingapp.core.ui.components.cards.PromoCodeCoupon
import krio.systemdesign.shoppingapp.core.ui.components.cards.SectionCard
import krio.systemdesign.shoppingapp.core.ui.components.notices.Notice
import krio.systemdesign.shoppingapp.core.ui.components.notices.NoticeStyle
import krio.systemdesign.shoppingapp.core.ui.icons.AppIcons
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.ConfirmationNumber
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.HourglassEmpty
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Inventory2
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Link
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.OpenInNew
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.ProductionQuantityLimits
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Sell
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.ShoppingBag
import krio.systemdesign.shoppingapp.core.ui.text.formatPrice
import krio.systemdesign.shoppingapp.core.ui.theme.Spacing
import krio.systemdesign.shoppingapp.uikit.R
import krio.systemdesign.shoppingapp.uikit.samples.SampleData
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup

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
        sampleGroup("ProductCard · catalog") {
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
                    name = stringResource(R.string.uikit_sample_product_hub),
                    imageUrl = SampleData.missingImageUrl,
                    price = formatPrice(SampleData.HUB_PRICE),
                    onClick = {},
                ) {
                    OutOfStockButton()
                }
            }
        }
        sampleGroup("ProductCard · cart") {
            SampleVariant(stringResource(R.string.uikit_variant_cart)) {
                ProductCard(
                    name = stringResource(R.string.uikit_sample_product_headphones),
                    imageUrl = SampleData.headphonesImageUrl,
                    price = formatPrice(SampleData.HEADPHONES_PRICE * 2),
                    onClick = {},
                    unitPrice = formatPrice(SampleData.HEADPHONES_PRICE),
                ) {
                    CartQuantityControl(
                        quantity = 2,
                        onAdd = {},
                        onIncrease = {},
                        onDecrease = {},
                        onRemoveAll = {},
                    )
                }
            }
        }
        sampleGroup("ProductCard · cart, one issue") {
            SampleVariant(stringResource(R.string.uikit_variant_cart_dimmed)) {
                ProductCard(
                    name = stringResource(R.string.uikit_sample_product_hub),
                    imageUrl = SampleData.missingImageUrl,
                    price = formatPrice(SampleData.HUB_PRICE),
                    onClick = {},
                    unitPrice = formatPrice(SampleData.HUB_PRICE),
                    isDimmed = true,
                ) {
                    Notice(
                        icon = AppIcons.Inventory2,
                        title = stringResource(R.string.uikit_sample_out_of_stock),
                        style = NoticeStyle.Error,
                    )
                    CartQuantityControl(
                        quantity = 1,
                        onAdd = {},
                        onIncrease = {},
                        onDecrease = {},
                        onRemoveAll = {},
                        canIncrease = false,
                    )
                }
            }
            SampleVariant(stringResource(R.string.uikit_variant_price_changed)) {
                ProductCard(
                    name = stringResource(R.string.uikit_sample_product_cutting_board),
                    imageUrl = SampleData.missingImageUrl,
                    price = formatPrice(SampleData.CUTTING_BOARD_OLD_PRICE),
                    onClick = {},
                    unitPrice = formatPrice(SampleData.CUTTING_BOARD_OLD_PRICE),
                ) {
                    Notice(
                        icon = AppIcons.Sell,
                        title = stringResource(
                            R.string.uikit_sample_price_changed,
                            formatPrice(SampleData.CUTTING_BOARD_PRICE),
                        ),
                        style = NoticeStyle.Error,
                    )
                    CartQuantityControl(
                        quantity = 1,
                        onAdd = {},
                        onIncrease = {},
                        onDecrease = {},
                        onRemoveAll = {},
                    )
                }
            }
            SampleVariant(stringResource(R.string.uikit_variant_not_enough_stock)) {
                ProductCard(
                    name = stringResource(R.string.uikit_sample_product_keyboard),
                    imageUrl = SampleData.keyboardImageUrl,
                    price = formatPrice(SampleData.KEYBOARD_PRICE * 2),
                    onClick = {},
                    unitPrice = formatPrice(SampleData.KEYBOARD_PRICE),
                ) {
                    Notice(
                        icon = AppIcons.ProductionQuantityLimits,
                        title = stringResource(R.string.uikit_sample_only_available, 1),
                        style = NoticeStyle.Error,
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
        sampleGroup("ProductCard · cart, several issues") {
            SampleVariant(stringResource(R.string.uikit_variant_several_issues)) {
                ProductCard(
                    name = stringResource(R.string.uikit_sample_product_notebook),
                    imageUrl = SampleData.missingImageUrl,
                    price = formatPrice(SampleData.NOTEBOOK_OLD_PRICE * 10),
                    onClick = {},
                    unitPrice = formatPrice(SampleData.NOTEBOOK_OLD_PRICE),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Notice(
                            icon = AppIcons.Sell,
                            title = stringResource(
                                R.string.uikit_sample_price_changed,
                                formatPrice(SampleData.NOTEBOOK_PRICE),
                            ),
                            style = NoticeStyle.Error,
                        )
                        Notice(
                            icon = AppIcons.ProductionQuantityLimits,
                            title = stringResource(R.string.uikit_sample_only_available, 5),
                            style = NoticeStyle.Error,
                        )
                    }
                    CartQuantityControl(
                        quantity = 10,
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
                    icon = AppIcons.ShoppingBag,
                    title = stringResource(R.string.uikit_sample_order_items),
                ) {
                    SampleOrderItems()
                }
            }
            SampleVariant(stringResource(R.string.uikit_variant_with_subtitle)) {
                SectionCard(
                    icon = AppIcons.ConfirmationNumber,
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
        // AppListItem has its own padding, like list rows on screen, so the frame adds none.
        sampleGroup("AppListItem") {
            SampleVariant(stringResource(R.string.uikit_variant_trailing_icon), contentPadding = 0.dp) {
                AppListItem(
                    icon = AppIcons.Link,
                    title = stringResource(R.string.uikit_sample_deep_links),
                    description = stringResource(R.string.uikit_sample_deep_links_description),
                    onClick = {},
                    trailing = { Icon(AppIcons.OpenInNew, contentDescription = null) },
                )
            }
            SampleVariant(stringResource(R.string.uikit_variant_trailing_choice), contentPadding = 0.dp) {
                var selectedSeconds by rememberSaveable { mutableIntStateOf(0) }
                AppListItem(
                    icon = AppIcons.HourglassEmpty,
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

// Two order rows as on the checkout screen: one item and two.
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

// Promo codes as on the promo code screen.
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
    Column(modifier = Modifier.padding(Spacing.CardPadding)) {
        Text(
            text = stringResource(R.string.uikit_sample_card_content),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
