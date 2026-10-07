package krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.checkout.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.Notice
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.NoticeStyle
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Info
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.feature.checkout.impl.R
import krio.systemdesign.shoppingapp.shared.ui.order.TotalBottomBar
import krio.systemdesign.shoppingapp.shared.ui.text.formatPrice

@Composable
internal fun CheckoutBottomBar(
    total: Long,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    onPlaceOrder: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TotalBottomBar(
        total = formatPrice(total),
        actionText = stringResource(R.string.checkout_place_order),
        enabled = canSubmit,
        isLoading = isSubmitting,
        onAction = onPlaceOrder,
        modifier = modifier,
        header = {
            // A neutral notice, not a colored one: it explains, it doesn't warn about a problem.
            Notice(
                icon = AppIcons.Info,
                title = stringResource(R.string.checkout_demo_order_notice),
                style = NoticeStyle.Neutral,
            )
        },
    )
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CheckoutBottomBarPreview() {
    ShoppingAppTheme {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Ready to place, street not filled in yet, placing.
            CheckoutBottomBar(total = 23395, canSubmit = true, isSubmitting = false, onPlaceOrder = {})
            CheckoutBottomBar(total = 23395, canSubmit = false, isSubmitting = false, onPlaceOrder = {})
            CheckoutBottomBar(total = 23395, canSubmit = false, isSubmitting = true, onPlaceOrder = {})
        }
    }
}
