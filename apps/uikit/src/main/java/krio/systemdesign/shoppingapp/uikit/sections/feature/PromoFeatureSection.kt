package krio.systemdesign.shoppingapp.uikit.sections.feature

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.components.loading.ShimmerPlaceholder
import krio.systemdesign.shoppingapp.feature.promo.ui.PromoCodeCoupon
import krio.systemdesign.shoppingapp.feature.promo.ui.PromoCodeCouponPlaceholder
import krio.systemdesign.shoppingapp.uikit.R
import krio.systemdesign.shoppingapp.uikit.samples.SampleData
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.sampleAnimationSwitch
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup

@Composable
internal fun PromoFeatureSection(innerPadding: PaddingValues) {
    var isAnimating by rememberSaveable { mutableStateOf(true) }
    SampleList(innerPadding) {
        sampleGroup("PromoCodeCoupon") {
            SampleVariant {
                SampleCoupons()
            }
        }
        sampleAnimationSwitch(isAnimating = isAnimating, onCheckedChange = { isAnimating = it })
        sampleGroup("PromoCodeCouponPlaceholder") {
            SampleVariant(caption = stringResource(R.string.uikit_variant_coupons_placeholder)) {
                ShimmerPlaceholder(isAnimating = isAnimating) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(2) {
                            PromoCodeCouponPlaceholder()
                        }
                    }
                }
            }
        }
    }
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
