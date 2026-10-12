package krio.systemdesign.shoppingapp.uikit.sections.crossfeature

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.components.loading.ShimmerPlaceholder
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.Notice
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.NoticeStyle
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Inventory2
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.ProductionQuantityLimits
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Sell
import krio.systemdesign.shoppingapp.crossfeature.ui.cart.CartQuantityControl
import krio.systemdesign.shoppingapp.crossfeature.ui.product.ProductCard
import krio.systemdesign.shoppingapp.crossfeature.ui.product.ProductCardPlaceholder
import krio.systemdesign.shoppingapp.crossfeature.ui.product.ProductImage
import krio.systemdesign.shoppingapp.crossfeature.ui.product.ProductImagePlaceholder
import krio.systemdesign.shoppingapp.crossfeature.ui.text.formatPrice
import krio.systemdesign.shoppingapp.feature.catalog.ui.OutOfStockButton
import krio.systemdesign.shoppingapp.uikit.resources.Res
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_only_available
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_out_of_stock
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_price_changed
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_product_cutting_board
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_product_headphones
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_product_hub
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_product_keyboard
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_product_notebook
import krio.systemdesign.shoppingapp.uikit.resources.uikit_variant_cart
import krio.systemdesign.shoppingapp.uikit.resources.uikit_variant_cart_dimmed
import krio.systemdesign.shoppingapp.uikit.resources.uikit_variant_catalog
import krio.systemdesign.shoppingapp.uikit.resources.uikit_variant_catalog_out_of_stock
import krio.systemdesign.shoppingapp.uikit.resources.uikit_variant_load_failed
import krio.systemdesign.shoppingapp.uikit.resources.uikit_variant_loaded
import krio.systemdesign.shoppingapp.uikit.resources.uikit_variant_not_enough_stock
import krio.systemdesign.shoppingapp.uikit.resources.uikit_variant_price_changed
import krio.systemdesign.shoppingapp.uikit.resources.uikit_variant_product_image_placeholder
import krio.systemdesign.shoppingapp.uikit.resources.uikit_variant_several_issues
import krio.systemdesign.shoppingapp.uikit.samples.SampleData
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.sampleAnimationSwitch
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ProductSection(innerPadding: PaddingValues) {
    var isAnimating by rememberSaveable { mutableStateOf(true) }
    SampleList(innerPadding) {
        sampleGroup("ProductCard · catalog") {
            SampleVariant(caption = stringResource(Res.string.uikit_variant_catalog)) {
                var quantity by rememberSaveable { mutableIntStateOf(0) }
                ProductCard(
                    name = stringResource(Res.string.uikit_sample_product_headphones),
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
            SampleVariant(caption = stringResource(Res.string.uikit_variant_catalog_out_of_stock)) {
                ProductCard(
                    name = stringResource(Res.string.uikit_sample_product_hub),
                    imageUrl = SampleData.MISSING_IMAGE_URL,
                    price = formatPrice(SampleData.HUB_PRICE),
                    onClick = {},
                ) {
                    OutOfStockButton()
                }
            }
        }
        sampleGroup("ProductCard · cart") {
            SampleVariant(caption = stringResource(Res.string.uikit_variant_cart)) {
                ProductCard(
                    name = stringResource(Res.string.uikit_sample_product_headphones),
                    imageUrl = SampleData.headphonesImageUrl,
                    price = formatPrice(SampleData.HEADPHONES_PRICE * 2),
                    onClick = {},
                    unitPrice = formatPrice(SampleData.HEADPHONES_PRICE),
                ) {
                    StaticQuantityControl(quantity = 2)
                }
            }
        }
        sampleGroup("ProductCard · cart, one issue") {
            SampleVariant(caption = stringResource(Res.string.uikit_variant_cart_dimmed)) {
                ProductCard(
                    name = stringResource(Res.string.uikit_sample_product_hub),
                    imageUrl = SampleData.MISSING_IMAGE_URL,
                    price = formatPrice(SampleData.HUB_PRICE),
                    onClick = {},
                    unitPrice = formatPrice(SampleData.HUB_PRICE),
                    isDimmed = true,
                ) {
                    Notice(
                        icon = AppIcons.Inventory2,
                        title = stringResource(Res.string.uikit_sample_out_of_stock),
                        style = NoticeStyle.Error,
                    )
                    StaticQuantityControl(quantity = 1, canIncrease = false)
                }
            }
            SampleVariant(caption = stringResource(Res.string.uikit_variant_price_changed)) {
                ProductCard(
                    name = stringResource(Res.string.uikit_sample_product_cutting_board),
                    imageUrl = SampleData.MISSING_IMAGE_URL,
                    price = formatPrice(SampleData.CUTTING_BOARD_OLD_PRICE),
                    onClick = {},
                    unitPrice = formatPrice(SampleData.CUTTING_BOARD_OLD_PRICE),
                ) {
                    Notice(
                        icon = AppIcons.Sell,
                        title = stringResource(
                            Res.string.uikit_sample_price_changed,
                            formatPrice(SampleData.CUTTING_BOARD_PRICE),
                        ),
                        style = NoticeStyle.Error,
                    )
                    StaticQuantityControl(quantity = 1)
                }
            }
            SampleVariant(caption = stringResource(Res.string.uikit_variant_not_enough_stock)) {
                ProductCard(
                    name = stringResource(Res.string.uikit_sample_product_keyboard),
                    imageUrl = SampleData.keyboardImageUrl,
                    price = formatPrice(SampleData.KEYBOARD_PRICE * 2),
                    onClick = {},
                    unitPrice = formatPrice(SampleData.KEYBOARD_PRICE),
                ) {
                    Notice(
                        icon = AppIcons.ProductionQuantityLimits,
                        title = stringResource(Res.string.uikit_sample_only_available, 1),
                        style = NoticeStyle.Error,
                    )
                    StaticQuantityControl(quantity = 2, canIncrease = false)
                }
            }
        }
        sampleGroup("ProductCard · cart, several issues") {
            SampleVariant(caption = stringResource(Res.string.uikit_variant_several_issues)) {
                ProductCard(
                    name = stringResource(Res.string.uikit_sample_product_notebook),
                    imageUrl = SampleData.MISSING_IMAGE_URL,
                    price = formatPrice(SampleData.NOTEBOOK_OLD_PRICE * 10),
                    onClick = {},
                    unitPrice = formatPrice(SampleData.NOTEBOOK_OLD_PRICE),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Notice(
                            icon = AppIcons.Sell,
                            title = stringResource(
                                Res.string.uikit_sample_price_changed,
                                formatPrice(SampleData.NOTEBOOK_PRICE),
                            ),
                            style = NoticeStyle.Error,
                        )
                        Notice(
                            icon = AppIcons.ProductionQuantityLimits,
                            title = stringResource(Res.string.uikit_sample_only_available, 5),
                            style = NoticeStyle.Error,
                        )
                    }
                    StaticQuantityControl(quantity = 10, canIncrease = false)
                }
            }
        }
        sampleGroup("ProductImage") {
            SampleVariant(caption = stringResource(Res.string.uikit_variant_loaded)) {
                ProductImage(
                    imageUrl = SampleData.headphonesImageUrl,
                    modifier = Modifier.size(SAMPLE_IMAGE_SIZE),
                )
            }
            SampleVariant(caption = stringResource(Res.string.uikit_variant_load_failed)) {
                ProductImage(
                    imageUrl = SampleData.MISSING_IMAGE_URL,
                    modifier = Modifier.size(SAMPLE_IMAGE_SIZE),
                )
            }
        }
        sampleAnimationSwitch(isAnimating = isAnimating, onCheckedChange = { isAnimating = it })
        sampleGroup("ProductCardPlaceholder") {
            SampleVariant {
                ProductCardPlaceholder(isAnimating = isAnimating)
            }
        }
        sampleGroup("ProductImagePlaceholder") {
            SampleVariant(caption = stringResource(Res.string.uikit_variant_product_image_placeholder)) {
                ShimmerPlaceholder(isAnimating = isAnimating) {
                    ProductImagePlaceholder(modifier = Modifier.size(88.dp))
                }
            }
        }
    }
}

// Cart buttons that only show a quantity: the cart samples aren't interactive.
@Composable
private fun StaticQuantityControl(
    quantity: Int,
    canIncrease: Boolean = true,
) {
    CartQuantityControl(
        quantity = quantity,
        onAdd = {},
        onIncrease = {},
        onDecrease = {},
        onRemoveAll = {},
        canIncrease = canIncrease,
    )
}

private val SAMPLE_IMAGE_SIZE = 120.dp
