package krio.systemdesign.shoppingapp.uikit.sections

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import krio.systemdesign.shoppingapp.core.ui.components.AppliedPromoCodeNotice
import krio.systemdesign.shoppingapp.core.ui.components.ErrorBanner
import krio.systemdesign.shoppingapp.core.ui.components.Notice
import krio.systemdesign.shoppingapp.core.ui.components.NoticeWithAction
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
                    icon = Icons.Outlined.Inventory2,
                    title = stringResource(R.string.uikit_sample_out_of_stock),
                    accentColor = MaterialTheme.colorScheme.error,
                )
                Notice(
                    icon = Icons.Outlined.Sell,
                    title = stringResource(R.string.uikit_sample_price_changed, formatPrice(SampleData.KEYBOARD_PRICE)),
                    accentColor = MaterialTheme.colorScheme.error,
                )
            }
            SampleVariant(stringResource(R.string.uikit_variant_neutral)) {
                Notice(
                    icon = Icons.Outlined.Info,
                    title = stringResource(R.string.uikit_sample_demo_order),
                    accentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        sampleGroup("NoticeWithAction") {
            SampleVariant(stringResource(R.string.uikit_variant_with_action)) {
                NoticeWithAction(
                    icon = Icons.Outlined.Sell,
                    title = stringResource(R.string.uikit_sample_price_changes),
                    accentColor = MaterialTheme.colorScheme.error,
                    actionText = stringResource(R.string.uikit_sample_accept),
                    onAction = {},
                )
            }
            SampleVariant(stringResource(R.string.uikit_variant_with_subtitle)) {
                NoticeWithAction(
                    icon = Icons.Outlined.ErrorOutline,
                    title = stringResource(R.string.uikit_sample_promo_invalid, SampleData.PROMO_CODE),
                    subtitle = stringResource(R.string.uikit_sample_promo_invalid_hint),
                    accentColor = MaterialTheme.colorScheme.error,
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
