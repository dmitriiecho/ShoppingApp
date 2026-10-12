package krio.systemdesign.shoppingapp.uikit.sections.crossfeature

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.Notice
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.NoticeStyle
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Info
import krio.systemdesign.shoppingapp.crossfeature.ui.order.OrderTotals
import krio.systemdesign.shoppingapp.crossfeature.ui.order.TotalBottomBar
import krio.systemdesign.shoppingapp.crossfeature.ui.text.formatPrice
import krio.systemdesign.shoppingapp.uikit.resources.Res
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_checkout
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_demo_order
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_place_order
import krio.systemdesign.shoppingapp.uikit.resources.uikit_variant_tap_to_load
import krio.systemdesign.shoppingapp.uikit.resources.uikit_variant_with_discount
import krio.systemdesign.shoppingapp.uikit.resources.uikit_variant_with_header
import krio.systemdesign.shoppingapp.uikit.resources.uikit_variant_without_discount
import krio.systemdesign.shoppingapp.uikit.samples.SampleData
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.rememberSampleLoading
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun OrderSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        sampleGroup("OrderTotals") {
            SampleVariant(caption = stringResource(Res.string.uikit_variant_with_discount)) {
                val subtotal = SampleData.HEADPHONES_PRICE
                val discount = subtotal * SampleData.PROMO_DISCOUNT_PERCENT / 100
                OrderTotals(
                    subtotal = formatPrice(subtotal),
                    total = formatPrice(subtotal - discount),
                    discount = formatPrice(discount),
                )
            }
            SampleVariant(caption = stringResource(Res.string.uikit_variant_without_discount)) {
                OrderTotals(
                    subtotal = formatPrice(SampleData.HEADPHONES_PRICE),
                    total = formatPrice(SampleData.HEADPHONES_PRICE),
                )
            }
        }
        sampleGroup("TotalBottomBar") {
            SampleVariant(caption = stringResource(Res.string.uikit_variant_tap_to_load)) {
                var isLoading by rememberSampleLoading()
                TotalBottomBar(
                    total = formatPrice(SampleData.HEADPHONES_PRICE),
                    actionText = stringResource(Res.string.uikit_sample_checkout),
                    enabled = !isLoading,
                    isLoading = isLoading,
                    onAction = { isLoading = true },
                )
            }
            SampleVariant(caption = stringResource(Res.string.uikit_variant_with_header)) {
                TotalBottomBar(
                    total = formatPrice(SampleData.HEADPHONES_PRICE),
                    actionText = stringResource(Res.string.uikit_sample_place_order),
                    enabled = true,
                    isLoading = false,
                    onAction = {},
                    header = {
                        Notice(
                            icon = AppIcons.Info,
                            title = stringResource(Res.string.uikit_sample_demo_order),
                            style = NoticeStyle.Neutral,
                        )
                    },
                )
            }
        }
    }
}
