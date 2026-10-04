package krio.systemdesign.shoppingapp.uikit.sections

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.R as CoreUiR
import krio.systemdesign.shoppingapp.core.ui.components.notices.AppliedPromoCodeNotice
import krio.systemdesign.shoppingapp.core.ui.components.notices.ErrorBanner
import krio.systemdesign.shoppingapp.core.ui.components.notices.Notice
import krio.systemdesign.shoppingapp.core.ui.components.notices.NoticeStyle
import krio.systemdesign.shoppingapp.core.ui.components.notices.NoticeWithAction
import krio.systemdesign.shoppingapp.core.ui.icons.AppIcons
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.CheckCircle
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Error
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Info
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Inventory2
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.ProductionQuantityLimits
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Sell
import krio.systemdesign.shoppingapp.core.ui.text.formatPrice
import krio.systemdesign.shoppingapp.uikit.R
import krio.systemdesign.shoppingapp.uikit.samples.SampleData
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup

// The same icons and texts as the notices on the app's screens, grouped like the Notice previews:
// product card, promo code, cart bar, checkout.
@Composable
fun NoticesSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        sampleGroup("Notice · product card") {
            SampleVariant {
                Notice(
                    icon = AppIcons.Inventory2,
                    title = stringResource(R.string.uikit_sample_out_of_stock),
                    style = NoticeStyle.Error,
                )
                Notice(
                    icon = AppIcons.Sell,
                    title = stringResource(
                        R.string.uikit_sample_price_changed,
                        formatPrice(SampleData.HEADPHONES_PRICE),
                    ),
                    style = NoticeStyle.Error,
                )
                Notice(
                    icon = AppIcons.ProductionQuantityLimits,
                    title = stringResource(R.string.uikit_sample_only_available, 1),
                    style = NoticeStyle.Error,
                )
            }
        }
        sampleGroup("Notice · promo code") {
            SampleVariant {
                NoticeWithAction(
                    icon = AppIcons.Error,
                    title = stringResource(R.string.uikit_sample_promo_invalid, SampleData.PROMO_CODE),
                    subtitle = stringResource(R.string.uikit_sample_promo_invalid_hint),
                    style = NoticeStyle.Error,
                    actionText = stringResource(R.string.uikit_sample_remove),
                    onAction = {},
                )
                NoticeWithAction(
                    icon = AppIcons.CheckCircle,
                    title = stringResource(
                        CoreUiR.string.core_ui_applied_promo_code,
                        SampleData.PROMO_CODE,
                        SampleData.PROMO_DISCOUNT_PERCENT,
                    ),
                    style = NoticeStyle.Success,
                    actionText = stringResource(R.string.uikit_sample_remove),
                    onAction = {},
                )
                Notice(
                    icon = AppIcons.CheckCircle,
                    title = stringResource(
                        CoreUiR.string.core_ui_applied_promo_code,
                        SampleData.SECOND_PROMO_CODE,
                        SampleData.SECOND_PROMO_DISCOUNT_PERCENT,
                    ),
                    style = NoticeStyle.Success,
                )
            }
        }
        sampleGroup("Notice · cart bar") {
            SampleVariant {
                NoticeWithAction(
                    icon = AppIcons.Sell,
                    title = stringResource(R.string.uikit_sample_price_changes),
                    style = NoticeStyle.Error,
                    actionText = stringResource(R.string.uikit_sample_accept),
                    onAction = {},
                    modifier = Modifier.heightIn(min = 56.dp),
                )
                NoticeWithAction(
                    icon = AppIcons.Inventory2,
                    title = stringResource(R.string.uikit_sample_item_out_of_stock),
                    style = NoticeStyle.Error,
                    actionText = stringResource(R.string.uikit_sample_remove),
                    onAction = {},
                    modifier = Modifier.heightIn(min = 56.dp),
                )
                Notice(
                    icon = AppIcons.ProductionQuantityLimits,
                    title = stringResource(R.string.uikit_sample_not_enough_stock_items),
                    style = NoticeStyle.Error,
                    modifier = Modifier.heightIn(min = 56.dp),
                )
            }
        }
        sampleGroup("Notice · checkout") {
            SampleVariant {
                Notice(
                    icon = AppIcons.Info,
                    title = stringResource(R.string.uikit_sample_demo_order),
                    style = NoticeStyle.Neutral,
                )
            }
        }
        sampleGroup("AppliedPromoCodeNotice") {
            SampleVariant(stringResource(R.string.uikit_variant_with_remove)) {
                AppliedPromoCodeNotice(
                    code = SampleData.PROMO_CODE,
                    discountPercent = SampleData.PROMO_DISCOUNT_PERCENT,
                    onRemove = {},
                )
            }
            SampleVariant(stringResource(R.string.uikit_variant_read_only)) {
                AppliedPromoCodeNotice(
                    code = SampleData.SECOND_PROMO_CODE,
                    discountPercent = SampleData.SECOND_PROMO_DISCOUNT_PERCENT,
                )
            }
        }
        sampleGroup("ErrorBanner") {
            SampleVariant {
                ErrorBanner(
                    message = stringResource(R.string.uikit_sample_prepend_error),
                    onRetry = {},
                )
            }
        }
    }
}
