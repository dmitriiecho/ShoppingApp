package krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.checkout.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.components.cards.SectionCard
import krio.systemdesign.shoppingapp.core.designsystem.components.inputs.AppTextField
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.LocationOn
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.designsystem.theme.Spacing
import krio.systemdesign.shoppingapp.feature.checkout.impl.R
import krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.checkout.CheckoutUiState

@Composable
internal fun DeliveryAddressSection(
    address: CheckoutUiState.Address,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    SectionCard(
        icon = AppIcons.LocationOn,
        title = stringResource(R.string.checkout_delivery_address),
        modifier = modifier,
    ) {
        // Street and apartment share a row: the apartment is short, a row of its own would be mostly empty.
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AppTextField(
                state = address.street,
                label = stringResource(R.string.checkout_street),
                enabled = enabled,
                modifier = Modifier.weight(1f),
            )
            AppTextField(
                state = address.apartment,
                label = stringResource(R.string.checkout_apartment),
                enabled = enabled,
                modifier = Modifier.width(APARTMENT_FIELD_WIDTH),
            )
        }
        AppTextField(
            state = address.courierComment,
            label = stringResource(R.string.checkout_courier_comment),
            enabled = enabled,
            singleLine = false,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private val APARTMENT_FIELD_WIDTH = 120.dp

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DeliveryAddressSectionPreview() {
    ShoppingAppTheme {
        Surface {
            DeliveryAddressSection(
                address = CheckoutUiState.Address(
                    street = rememberTextFieldState("Baker Street, 221"),
                    apartment = rememberTextFieldState("B"),
                    courierComment = rememberTextFieldState(),
                ),
                enabled = true,
                modifier = Modifier.padding(Spacing.ScreenPadding),
            )
        }
    }
}
