package krio.systemdesign.shoppingapp.uikit.sections.feature

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import krio.systemdesign.shoppingapp.feature.checkout.ui.OrderItemRow
import krio.systemdesign.shoppingapp.shared.ui.text.formatPrice
import krio.systemdesign.shoppingapp.uikit.R
import krio.systemdesign.shoppingapp.uikit.samples.SampleData
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup

@Composable
internal fun CheckoutFeatureSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        sampleGroup("OrderItemRow") {
            SampleVariant {
                SampleOrderItems()
            }
        }
    }
}

// Two order rows as on the checkout screen: one item and two.
@Composable
private fun SampleOrderItems() {
    OrderItemRow(
        name = stringResource(R.string.uikit_sample_product_headphones),
        imageUrl = SampleData.headphonesImageUrl,
        quantity = 1,
        unitPrice = formatPrice(SampleData.HEADPHONES_PRICE),
        total = formatPrice(SampleData.HEADPHONES_PRICE),
    )
    OrderItemRow(
        name = stringResource(R.string.uikit_sample_product_keyboard),
        imageUrl = SampleData.keyboardImageUrl,
        quantity = 2,
        unitPrice = formatPrice(SampleData.KEYBOARD_PRICE),
        total = formatPrice(SampleData.KEYBOARD_PRICE * 2),
    )
}
