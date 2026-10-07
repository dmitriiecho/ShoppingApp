package krio.systemdesign.shoppingapp.uikit.sections.shared

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import krio.systemdesign.shoppingapp.core.designsystem.components.loading.ShimmerPlaceholder
import krio.systemdesign.shoppingapp.shared.ui.cart.CartQuantityControl
import krio.systemdesign.shoppingapp.shared.ui.cart.CartQuantityControlPlaceholder
import krio.systemdesign.shoppingapp.uikit.R
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.sampleAnimationSwitch
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup

@Composable
fun CartSection(innerPadding: PaddingValues) {
    var isAnimating by rememberSaveable { mutableStateOf(true) }
    SampleList(innerPadding) {
        sampleGroup("CartQuantityControl") {
            SampleVariant(caption = stringResource(R.string.uikit_variant_interactive_cart)) {
                var quantity by rememberSaveable { mutableIntStateOf(0) }
                CartQuantityControl(
                    quantity = quantity,
                    onAdd = { quantity = 1 },
                    onIncrease = { quantity++ },
                    onDecrease = { quantity-- },
                    onRemoveAll = { quantity = 0 },
                    canIncrease = quantity < SAMPLE_AVAILABLE_QUANTITY,
                )
            }
        }
        sampleAnimationSwitch(isAnimating = isAnimating, onCheckedChange = { isAnimating = it })
        sampleGroup("CartQuantityControlPlaceholder") {
            SampleVariant(caption = stringResource(R.string.uikit_variant_cart_buttons_placeholder)) {
                ShimmerPlaceholder(isAnimating = isAnimating) {
                    CartQuantityControlPlaceholder()
                }
            }
        }
    }
}

// Stock in the CartQuantityControl sample: "+" turns off at this quantity.
private const val SAMPLE_AVAILABLE_QUANTITY = 3
