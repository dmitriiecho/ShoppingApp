package krio.systemdesign.shoppingapp.uikit.sections

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import krio.systemdesign.shoppingapp.core.ui.components.AppliedPromoCodeNotice
import krio.systemdesign.shoppingapp.core.ui.components.ErrorBanner
import krio.systemdesign.shoppingapp.core.ui.components.Notice
import krio.systemdesign.shoppingapp.core.ui.components.NoticeStyle
import krio.systemdesign.shoppingapp.core.ui.components.NoticeWithAction
import krio.systemdesign.shoppingapp.core.ui.icons.AppIcons
import krio.systemdesign.shoppingapp.core.ui.text.formatPrice
import krio.systemdesign.shoppingapp.uikit.R
import krio.systemdesign.shoppingapp.uikit.components.SampleData
import krio.systemdesign.shoppingapp.uikit.components.SampleList
import krio.systemdesign.shoppingapp.uikit.components.SampleVariant
import krio.systemdesign.shoppingapp.uikit.components.sampleGroup

// Значки и тексты те же, что у плашек на экранах приложения: у каждого значка там один смысл.
@Composable
fun NoticesSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        sampleGroup("Notice") {
            SampleVariant(stringResource(R.string.uikit_variant_error)) {
                Notice(
                    icon = AppIcons.OutOfStock,
                    title = stringResource(R.string.uikit_sample_out_of_stock),
                    style = NoticeStyle.Error,
                )
                Notice(
                    icon = AppIcons.PriceChanged,
                    title = stringResource(R.string.uikit_sample_price_changed, formatPrice(SampleData.KEYBOARD_PRICE)),
                    style = NoticeStyle.Error,
                )
            }
            SampleVariant(stringResource(R.string.uikit_variant_neutral)) {
                Notice(
                    icon = AppIcons.Info,
                    title = stringResource(R.string.uikit_sample_demo_order),
                    style = NoticeStyle.Neutral,
                )
            }
        }
        sampleGroup("NoticeWithAction") {
            SampleVariant(stringResource(R.string.uikit_variant_with_action)) {
                NoticeWithAction(
                    icon = AppIcons.PriceChanged,
                    title = stringResource(R.string.uikit_sample_price_changes),
                    style = NoticeStyle.Error,
                    actionText = stringResource(R.string.uikit_sample_accept),
                    onAction = {},
                )
            }
            SampleVariant(stringResource(R.string.uikit_variant_with_subtitle)) {
                NoticeWithAction(
                    icon = AppIcons.Error,
                    title = stringResource(R.string.uikit_sample_promo_invalid, SampleData.PROMO_CODE),
                    subtitle = stringResource(R.string.uikit_sample_promo_invalid_hint),
                    style = NoticeStyle.Error,
                    actionText = stringResource(R.string.uikit_sample_remove),
                    onAction = {},
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
                    code = SampleData.PROMO_CODE,
                    discountPercent = SampleData.PROMO_DISCOUNT_PERCENT,
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
