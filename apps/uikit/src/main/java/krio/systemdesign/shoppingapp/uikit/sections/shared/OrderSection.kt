package krio.systemdesign.shoppingapp.uikit.sections.shared

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.Notice
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.NoticeStyle
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Info
import krio.systemdesign.shoppingapp.shared.ui.order.OrderTotals
import krio.systemdesign.shoppingapp.shared.ui.order.TotalBottomBar
import krio.systemdesign.shoppingapp.shared.ui.text.formatPrice
import krio.systemdesign.shoppingapp.uikit.R
import krio.systemdesign.shoppingapp.uikit.samples.SampleData
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.rememberSampleLoading
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup

@Composable
internal fun OrderSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        sampleGroup("OrderTotals") {
            SampleVariant(caption = stringResource(R.string.uikit_variant_with_discount)) {
                val subtotal = SampleData.HEADPHONES_PRICE
                val discount = subtotal * SampleData.PROMO_DISCOUNT_PERCENT / 100
                OrderTotals(
                    subtotal = formatPrice(subtotal),
                    total = formatPrice(subtotal - discount),
                    discount = formatPrice(discount),
                )
            }
            SampleVariant(caption = stringResource(R.string.uikit_variant_without_discount)) {
                OrderTotals(
                    subtotal = formatPrice(SampleData.HEADPHONES_PRICE),
                    total = formatPrice(SampleData.HEADPHONES_PRICE),
                )
            }
        }
        sampleGroup("TotalBottomBar") {
            SampleVariant(caption = stringResource(R.string.uikit_variant_tap_to_load)) {
                var isLoading by rememberSampleLoading()
                TotalBottomBar(
                    total = formatPrice(SampleData.HEADPHONES_PRICE),
                    actionText = stringResource(R.string.uikit_sample_checkout),
                    enabled = !isLoading,
                    isLoading = isLoading,
                    onAction = { isLoading = true },
                )
            }
            SampleVariant(caption = stringResource(R.string.uikit_variant_with_header)) {
                TotalBottomBar(
                    total = formatPrice(SampleData.HEADPHONES_PRICE),
                    actionText = stringResource(R.string.uikit_sample_place_order),
                    enabled = true,
                    isLoading = false,
                    onAction = {},
                    header = {
                        Notice(
                            icon = AppIcons.Info,
                            title = stringResource(R.string.uikit_sample_demo_order),
                            style = NoticeStyle.Neutral,
                        )
                    },
                )
            }
        }
    }
}
