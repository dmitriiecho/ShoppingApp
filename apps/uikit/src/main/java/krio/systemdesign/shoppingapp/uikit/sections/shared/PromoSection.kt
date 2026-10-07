package krio.systemdesign.shoppingapp.uikit.sections.shared

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import krio.systemdesign.shoppingapp.shared.ui.promo.AppliedPromoCodeNotice
import krio.systemdesign.shoppingapp.uikit.R
import krio.systemdesign.shoppingapp.uikit.samples.SampleData
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup

@Composable
fun PromoSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        sampleGroup("AppliedPromoCodeNotice") {
            SampleVariant(caption = stringResource(R.string.uikit_variant_with_remove)) {
                AppliedPromoCodeNotice(
                    code = SampleData.PROMO_CODE,
                    discountPercent = SampleData.PROMO_DISCOUNT_PERCENT,
                    onRemove = {},
                )
            }
            SampleVariant(caption = stringResource(R.string.uikit_variant_read_only)) {
                AppliedPromoCodeNotice(
                    code = SampleData.SECOND_PROMO_CODE,
                    discountPercent = SampleData.SECOND_PROMO_DISCOUNT_PERCENT,
                )
            }
        }
    }
}
