package krio.systemdesign.shoppingapp.uikit.sections.crossfeature

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import krio.systemdesign.shoppingapp.crossfeature.ui.promo.AppliedPromoCodeNotice
import krio.systemdesign.shoppingapp.uikit.resources.Res
import krio.systemdesign.shoppingapp.uikit.resources.uikit_variant_read_only
import krio.systemdesign.shoppingapp.uikit.resources.uikit_variant_with_remove
import krio.systemdesign.shoppingapp.uikit.samples.SampleData
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PromoSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        sampleGroup("AppliedPromoCodeNotice") {
            SampleVariant(caption = stringResource(Res.string.uikit_variant_with_remove)) {
                AppliedPromoCodeNotice(
                    code = SampleData.PROMO_CODE,
                    discountPercent = SampleData.PROMO_DISCOUNT_PERCENT,
                    onRemove = {},
                )
            }
            SampleVariant(caption = stringResource(Res.string.uikit_variant_read_only)) {
                AppliedPromoCodeNotice(
                    code = SampleData.SECOND_PROMO_CODE,
                    discountPercent = SampleData.SECOND_PROMO_DISCOUNT_PERCENT,
                )
            }
        }
    }
}
