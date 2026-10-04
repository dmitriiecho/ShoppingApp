package krio.systemdesign.shoppingapp.uikit.sections

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.components.buttons.CartQuantityControl
import krio.systemdesign.shoppingapp.core.ui.components.buttons.CloseIconButton
import krio.systemdesign.shoppingapp.core.ui.components.buttons.LoadingButton
import krio.systemdesign.shoppingapp.core.ui.components.buttons.NavigateBackIconButton
import krio.systemdesign.shoppingapp.core.ui.components.buttons.OutOfStockButton
import krio.systemdesign.shoppingapp.core.ui.components.buttons.ScrollToTopButton
import krio.systemdesign.shoppingapp.core.ui.components.buttons.SingleChoiceButtons
import krio.systemdesign.shoppingapp.core.ui.icons.AppIcons
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.CreditCard
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Payments
import krio.systemdesign.shoppingapp.uikit.R
import krio.systemdesign.shoppingapp.uikit.samples.SampleData
import krio.systemdesign.shoppingapp.uikit.samples.rememberSampleLoading
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleSwitch
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup

@Composable
fun ButtonsSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        sampleGroup("CartQuantityControl") {
            SampleVariant(stringResource(R.string.uikit_variant_interactive_cart)) {
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
        sampleGroup("OutOfStockButton") {
            SampleVariant {
                OutOfStockButton()
            }
        }
        sampleGroup("LoadingButton") {
            SampleVariant(stringResource(R.string.uikit_variant_tap_to_load)) {
                var isLoading by rememberSampleLoading()
                LoadingButton(
                    text = stringResource(R.string.uikit_sample_place_order),
                    isLoading = isLoading,
                    onClick = { isLoading = true },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            SampleVariant(stringResource(R.string.uikit_variant_disabled)) {
                LoadingButton(
                    text = stringResource(R.string.uikit_sample_place_order),
                    isLoading = false,
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        sampleGroup("SingleChoiceButtons") {
            SampleVariant(stringResource(R.string.uikit_variant_icon_and_text)) {
                var selected by rememberSaveable { mutableStateOf(SamplePaymentMethod.Card) }
                SingleChoiceButtons(
                    options = SamplePaymentMethod.entries,
                    selected = selected,
                    onSelect = { selected = it },
                    modifier = Modifier.fillMaxWidth(),
                ) { method ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = method.icon,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(method.titleRes))
                    }
                }
            }
            SampleVariant(stringResource(R.string.uikit_variant_fixed_width)) {
                var selectedSeconds by rememberSaveable { mutableIntStateOf(0) }
                SingleChoiceButtons(
                    options = SampleData.DELAY_SECONDS,
                    selected = selectedSeconds,
                    onSelect = { selectedSeconds = it },
                    modifier = Modifier.width(168.dp),
                ) { seconds ->
                    Text(seconds.toString())
                }
            }
        }
        sampleGroup("CloseIconButton") {
            SampleVariant {
                CloseIconButton(onClick = {})
            }
        }
        sampleGroup("NavigateBackIconButton") {
            SampleVariant {
                NavigateBackIconButton(onClick = {})
            }
        }
        sampleGroup("ScrollToTopButton") {
            SampleVariant {
                var isVisible by rememberSaveable { mutableStateOf(true) }
                SampleSwitch(
                    label = stringResource(R.string.uikit_visible),
                    checked = isVisible,
                    onCheckedChange = { isVisible = it },
                )
                ScrollToTopButton(
                    visible = isVisible,
                    onClick = { isVisible = false },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            }
        }
    }
}

private enum class SamplePaymentMethod(val icon: ImageVector, @StringRes val titleRes: Int) {
    Card(AppIcons.CreditCard, R.string.uikit_sample_payment_card),
    Cash(AppIcons.Payments, R.string.uikit_sample_payment_cash),
}

// Stock in the CartQuantityControl sample: "+" turns off at this quantity.
private const val SAMPLE_AVAILABLE_QUANTITY = 3
