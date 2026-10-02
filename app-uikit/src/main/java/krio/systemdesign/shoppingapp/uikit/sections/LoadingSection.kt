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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.components.CartQuantityControlPlaceholder
import krio.systemdesign.shoppingapp.core.ui.components.ProductCardPlaceholder
import krio.systemdesign.shoppingapp.core.ui.components.ProductImage
import krio.systemdesign.shoppingapp.core.ui.components.PromoCodeCouponPlaceholder
import krio.systemdesign.shoppingapp.core.ui.components.ShimmerPlaceholder
import krio.systemdesign.shoppingapp.core.ui.components.shimmerShape
import krio.systemdesign.shoppingapp.uikit.R
import krio.systemdesign.shoppingapp.uikit.components.SampleData
import krio.systemdesign.shoppingapp.uikit.components.SampleList
import krio.systemdesign.shoppingapp.uikit.components.SampleSwitch
import krio.systemdesign.shoppingapp.uikit.components.SampleVariant
import krio.systemdesign.shoppingapp.uikit.components.sampleGroup

@Composable
fun LoadingSection(innerPadding: PaddingValues) {
    // Один переключатель на все заглушки: без анимации они такие, как после неудачной загрузки.
    var isAnimating by rememberSaveable { mutableStateOf(true) }
    SampleList(innerPadding) {
        item {
            SampleSwitch(
                label = stringResource(R.string.uikit_animation),
                checked = isAnimating,
                onCheckedChange = { isAnimating = it },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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
                                    .shimmerShape(RoundedCornerShape(4.dp)),
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .height(16.dp)
                                    .shimmerShape(RoundedCornerShape(4.dp)),
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
        sampleGroup("CartQuantityControlPlaceholder") {
            SampleVariant(stringResource(R.string.uikit_variant_cart_buttons_placeholder)) {
                ShimmerPlaceholder(isAnimating = isAnimating) {
                    CartQuantityControlPlaceholder()
                }
            }
        }
        sampleGroup("ProductImage") {
            SampleVariant(stringResource(R.string.uikit_variant_loaded)) {
                ProductImage(
                    imageUrl = SampleData.headphonesImageUrl,
                    contentDescription = stringResource(R.string.uikit_sample_product_headphones),
                    modifier = Modifier.size(SAMPLE_IMAGE_SIZE),
                )
            }
            SampleVariant(stringResource(R.string.uikit_variant_load_failed)) {
                ProductImage(
                    imageUrl = SampleData.missingImageUrl,
                    contentDescription = null,
                    modifier = Modifier.size(SAMPLE_IMAGE_SIZE),
                )
            }
        }
    }
}

private val SAMPLE_IMAGE_SIZE = 120.dp
