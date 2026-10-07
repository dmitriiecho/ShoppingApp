package krio.systemdesign.shoppingapp.uikit.sections.feature

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import krio.systemdesign.shoppingapp.core.designsystem.components.loading.ShimmerPlaceholder
import krio.systemdesign.shoppingapp.feature.catalog.ui.OutOfStockButton
import krio.systemdesign.shoppingapp.feature.catalog.ui.ProductInfoPlaceholder
import krio.systemdesign.shoppingapp.uikit.R
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.sampleAnimationSwitch
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup

@Composable
fun CatalogFeatureSection(innerPadding: PaddingValues) {
    var isAnimating by rememberSaveable { mutableStateOf(true) }
    SampleList(innerPadding) {
        sampleGroup("OutOfStockButton") {
            SampleVariant {
                OutOfStockButton()
            }
        }
        sampleAnimationSwitch(isAnimating = isAnimating, onCheckedChange = { isAnimating = it })
        sampleGroup("ProductInfoPlaceholder") {
            SampleVariant(caption = stringResource(R.string.uikit_variant_product_info_placeholder)) {
                ShimmerPlaceholder(
                    modifier = Modifier.fillMaxWidth(),
                    isAnimating = isAnimating,
                ) {
                    ProductInfoPlaceholder()
                }
            }
        }
    }
}
