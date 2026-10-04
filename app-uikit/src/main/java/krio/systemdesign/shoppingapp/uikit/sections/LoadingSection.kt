package krio.systemdesign.shoppingapp.uikit.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.components.buttons.CartQuantityControlPlaceholder
import krio.systemdesign.shoppingapp.core.ui.components.cards.ProductCardPlaceholder
import krio.systemdesign.shoppingapp.core.ui.components.cards.PromoCodeCouponPlaceholder
import krio.systemdesign.shoppingapp.core.ui.components.loading.ProductInfoPlaceholder
import krio.systemdesign.shoppingapp.core.ui.components.loading.ShimmerPlaceholder
import krio.systemdesign.shoppingapp.core.ui.components.loading.shimmerShape
import krio.systemdesign.shoppingapp.core.ui.theme.Spacing
import krio.systemdesign.shoppingapp.uikit.R
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleSwitch
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup

@Composable
fun LoadingSection(innerPadding: PaddingValues) {
    // One switch for all placeholders: without animation they look as after a failed load.
    var isAnimating by rememberSaveable { mutableStateOf(true) }
    SampleList(innerPadding) {
        item {
            SampleSwitch(
                label = stringResource(R.string.uikit_animation),
                checked = isAnimating,
                onCheckedChange = { isAnimating = it },
                modifier = Modifier.padding(horizontal = Spacing.ScreenPadding, vertical = 8.dp),
            )
        }
        sampleGroup("ShimmerPlaceholder · Modifier.shimmerShape") {
            SampleVariant(stringResource(R.string.uikit_variant_custom_shapes)) {
                ShimmerPlaceholder(
                    modifier = Modifier.fillMaxWidth(),
                    isAnimating = isAnimating,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .shimmerShape(CircleShape),
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(16.dp)
                                    .shimmerShape(MaterialTheme.shapes.extraSmall),
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .height(16.dp)
                                    .shimmerShape(MaterialTheme.shapes.extraSmall),
                            )
                        }
                    }
                }
            }
        }
        sampleGroup("ProductCardPlaceholder") {
            SampleVariant {
                ProductCardPlaceholder(isLoading = isAnimating)
            }
        }
        sampleGroup("PromoCodeCouponPlaceholder") {
            SampleVariant(stringResource(R.string.uikit_variant_coupons_placeholder)) {
                ShimmerPlaceholder(isAnimating = isAnimating) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(2) {
                            PromoCodeCouponPlaceholder()
                        }
                    }
                }
            }
        }
        sampleGroup("ProductInfoPlaceholder") {
            SampleVariant(stringResource(R.string.uikit_variant_product_info_placeholder)) {
                ShimmerPlaceholder(
                    modifier = Modifier.fillMaxWidth(),
                    isAnimating = isAnimating,
                ) {
                    ProductInfoPlaceholder()
                }
            }
        }
        sampleGroup("CartQuantityControlPlaceholder") {
            SampleVariant(stringResource(R.string.uikit_variant_cart_buttons_placeholder)) {
                ShimmerPlaceholder(isAnimating = isAnimating) {
                    CartQuantityControlPlaceholder()
                }
            }
        }
    }
}
