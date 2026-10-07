package krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.checkout.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.components.buttons.SingleChoiceButtons
import krio.systemdesign.shoppingapp.core.designsystem.components.cards.SectionCard
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.AccountBalanceWallet
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.CreditCard
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Payments
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.designsystem.theme.Spacing
import krio.systemdesign.shoppingapp.feature.checkout.impl.R
import krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.checkout.CheckoutUiState

@Composable
internal fun PaymentSection(
    selected: CheckoutUiState.PaymentMethod,
    onSelect: (CheckoutUiState.PaymentMethod) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    SectionCard(
        icon = AppIcons.AccountBalanceWallet,
        title = stringResource(R.string.checkout_payment),
        modifier = modifier,
    ) {
        SingleChoiceButtons(
            options = CheckoutUiState.PaymentMethod.entries,
            selected = selected,
            onSelect = onSelect,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
        ) { method ->
            // The button doesn't lay out its content in a row: without Row the icon and text would overlap.
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
}

private val CheckoutUiState.PaymentMethod.titleRes: Int
    get() = when (this) {
        CheckoutUiState.PaymentMethod.Card -> R.string.checkout_payment_card
        CheckoutUiState.PaymentMethod.Cash -> R.string.checkout_payment_cash
    }

private val CheckoutUiState.PaymentMethod.icon: ImageVector
    get() = when (this) {
        CheckoutUiState.PaymentMethod.Card -> AppIcons.CreditCard
        CheckoutUiState.PaymentMethod.Cash -> AppIcons.Payments
    }

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PaymentSectionPreview() {
    ShoppingAppTheme {
        Surface {
            PaymentSection(
                selected = CheckoutUiState.PaymentMethod.Card,
                onSelect = {},
                enabled = true,
                modifier = Modifier.padding(Spacing.ScreenPadding),
            )
        }
    }
}
